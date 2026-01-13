package com.example.internetjson

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: StudentAdapter
    private var originalList: List<Student> = ArrayList() // Danh sách gốc để lọc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Gọi API lấy dữ liệu
        fetchData()

        // Xử lý tìm kiếm
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }
        })
    }

    private fun fetchData() {
        RetrofitClient.instance.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful && response.body() != null) {
                    originalList = response.body()!!
                    setupAdapter(originalList)
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter(list: List<Student>) {
        adapter = StudentAdapter(list) { student ->
            // Mở màn hình chi tiết khi click
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("student_data", student)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    // Hàm lọc dữ liệu theo Tên hoặc MSSV
    private fun filterData(query: String?) {
        val searchText = query?.trim() ?: ""
        
        // 1. Chuyển từ khóa tìm kiếm về dạng không dấu
        val cleanQuery = removeVietnameseAccents(searchText)

        val filteredList = if (searchText.isEmpty()) {
            originalList
        } else {
            originalList.filter { student ->
                // 2. Chuyển tên và MSSV trong danh sách về dạng không dấu
                val cleanName = removeVietnameseAccents(student.hoten)
                val cleanMssv = removeVietnameseAccents(student.mssv)

                // 3. So sánh
                cleanName.contains(cleanQuery) || cleanMssv.contains(cleanQuery)
            }
        }

        // Cập nhật Adapter
        if (::adapter.isInitialized) {
            adapter.updateList(filteredList)
        }
    }
    
    private fun removeVietnameseAccents(str: String): String {
        try {
            var temp = Normalizer.normalize(str, Normalizer.Form.NFD)
            val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            temp = pattern.matcher(temp).replaceAll("")
            return temp.lowercase().replace("đ", "d").replace("Đ", "d")
        } catch (e: Exception) {
            return str
        }
    }
}
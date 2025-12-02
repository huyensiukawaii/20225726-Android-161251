// File: MainActivity.kt

package com.example.dssv_new

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter

    // Sử dụng biến tĩnh hoặc Singleton để giữ danh sách nếu cần dùng chung cho cả ứng dụng
    companion object {
        val studentList = mutableListOf<Student>()

        // Khởi tạo dữ liệu mẫu lần đầu
        init {
            studentList.add(Student("20200001", "Nguyễn Văn A", "0901234567", "Hà Nội"))
            studentList.add(Student("20200002", "Trần Thị B", "0909876543", "TP. Hồ Chí Minh"))
        }
    }

    // Contracts để nhận kết quả từ các Activity
    private val addStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newStudent = result.data?.getSerializableExtra("new_student") as? Student
            if (newStudent != null) {
                studentList.add(newStudent)
                studentAdapter.notifyItemInserted(studentList.size - 1)
                Toast.makeText(this, "Đã thêm sinh viên: ${newStudent.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val detailStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedStudent = result.data?.getSerializableExtra("updated_student") as? Student
            val position = result.data?.getIntExtra("student_position", -1) ?: -1

            if (updatedStudent != null && position != -1) {
                // Cập nhật dữ liệu trong list
                studentList[position] = updatedStudent
                studentAdapter.notifyItemChanged(position)
                Toast.makeText(this, "Đã cập nhật thông tin sinh viên: ${updatedStudent.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dùng layout cũ của bạn, nhưng bỏ các EditText và Button Add/Update
        setContentView(R.layout.activity_main)
        // Giả sử layout activity_main đã được sửa chỉ còn RecyclerView

        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
    }

    // 1. Cấu hình RecyclerView
    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            studentList,
            onStudentClick = { student, position ->
                // Mở DetailActivity khi click vào sinh viên
                openDetailActivity(student, position)
            },
            onDeleteClick = { position ->
                // Khi nhấn xóa
                deleteStudent(position)
            }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // 2. Xử lý Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Thêm menu item
        menu?.add(Menu.NONE, 1, Menu.NONE, "Thêm sinh viên")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                // Mở AddStudentActivity khi nhấn "Thêm sinh viên"
                openAddStudentActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 3. Xử lý mở Activity
    private fun openAddStudentActivity() {
        val intent = Intent(this, AddStudentActivity::class.java)
        addStudentLauncher.launch(intent) // Dùng launcher để nhận kết quả
    }

    private fun openDetailActivity(student: Student, position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("selected_student", student) // Truyền đối tượng Student
        intent.putExtra("student_position", position) // Truyền vị trí
        detailStudentLauncher.launch(intent) // Dùng launcher để nhận kết quả
    }

    // 4. Xử lý xóa
    private fun deleteStudent(position: Int) {
        val studentName = studentList[position].name
        studentList.removeAt(position)
        studentAdapter.notifyItemRemoved(position)
        // Cập nhật lại index cho các item bên dưới item bị xóa
        studentAdapter.notifyItemRangeChanged(position, studentList.size)
        Toast.makeText(this, "Đã xóa sinh viên: $studentName", Toast.LENGTH_SHORT).show()
    }
}
package com.example.dssv

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var edtMssv: EditText
    private lateinit var edtName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnUpdate: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var studentAdapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    // Biến lưu vị trí đang được chọn để sửa (-1 là chưa chọn gì)
    private var selectedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Ánh xạ View
        edtMssv = findViewById(R.id.edtMssv)
        edtName = findViewById(R.id.edtName)
        btnAdd = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
        recyclerView = findViewById(R.id.recyclerView)

        // Dữ liệu mẫu (nếu cần)
        studentList.add(Student("Nguyễn Văn A", "20200001"))
        studentList.add(Student("Trần Thị B", "20200002"))

        // 2. Cài đặt Adapter và RecyclerView
        setupRecyclerView()

        // 3. Xử lý sự kiện nút Add
        btnAdd.setOnClickListener {
            addStudent()
        }

        // 4. Xử lý sự kiện nút Update
        btnUpdate.setOnClickListener {
            updateStudent()
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            studentList,
            onStudentClick = { student, position ->
                // Khi nhấn vào một sinh viên: Đưa dữ liệu lên EditText và lưu vị trí
                edtName.setText(student.name)
                edtMssv.setText(student.mssv)
                selectedPosition = position
                // Khóa sửa MSSV nếu muốn (tuỳ chọn), ở đây đề bài chỉ yêu cầu cập nhật họ tên
                // edtMssv.isEnabled = false
            },
            onDeleteClick = { position ->
                // Khi nhấn xóa
                deleteStudent(position)
            }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun addStudent() {
        val name = edtName.text.toString().trim()
        val mssv = edtMssv.text.toString().trim()

        if (name.isNotEmpty() && mssv.isNotEmpty()) {
            val newStudent = Student(name, mssv)
            studentList.add(newStudent)
            // Thông báo cho adapter biết dữ liệu mới được thêm vào cuối
            studentAdapter.notifyItemInserted(studentList.size - 1)

            clearInput()
        } else {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStudent() {
        if (selectedPosition != -1) {
            val name = edtName.text.toString().trim()
            val mssv = edtMssv.text.toString().trim()

            if (name.isNotEmpty() && mssv.isNotEmpty()) {
                // Cập nhật dữ liệu trong list
                studentList[selectedPosition].name = name
                studentList[selectedPosition].mssv = mssv // Cập nhật cả MSSV nếu user sửa

                // Thông báo adapter cập nhật lại dòng đó
                studentAdapter.notifyItemChanged(selectedPosition)

                clearInput()
                selectedPosition = -1 // Reset trạng thái chọn
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Vui lòng chọn sinh viên để sửa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(position: Int) {
        studentList.removeAt(position)
        studentAdapter.notifyItemRemoved(position)
        // Cập nhật lại index cho các item bên dưới item bị xóa (để tránh lỗi vị trí)
        studentAdapter.notifyItemRangeChanged(position, studentList.size)

        // Nếu đang chọn sinh viên này để sửa mà lại xóa đi, thì clear input
        if (selectedPosition == position) {
            clearInput()
            selectedPosition = -1
        }
    }

    private fun clearInput() {
        edtName.text.clear()
        edtMssv.text.clear()
        edtMssv.requestFocus()
    }
}
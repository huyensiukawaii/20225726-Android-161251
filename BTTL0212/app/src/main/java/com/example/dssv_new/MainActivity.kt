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

    // Sử dụng Companion Object để giữ danh sách dùng chung (giả lập database)
    companion object {
        val studentList = mutableListOf<Student>()

        // Khởi tạo dữ liệu mẫu
        init {
            studentList.add(Student("20200001", "Nguyễn Văn A", "0901234567", "Hà Nội"))
            studentList.add(Student("20200002", "Trần Thị B", "0909876543", "TP. Hồ Chí Minh"))
        }
    }

    // 1. Launcher để nhận kết quả khi THÊM sinh viên (từ AddStudentActivity)
    private val addStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Lấy object Student được trả về
            val newStudent = result.data?.getSerializableExtra("new_student") as? Student
            if (newStudent != null) {
                studentList.add(newStudent)
                // Cập nhật giao diện: Thêm vào cuối danh sách
                studentAdapter.notifyItemInserted(studentList.size - 1)
                Toast.makeText(this, "Đã thêm: ${newStudent.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Launcher để nhận kết quả khi CẬP NHẬT sinh viên (từ DetailActivity)
    private val detailStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Lấy object Student đã sửa và vị trí của nó
            val updatedStudent = result.data?.getSerializableExtra("updated_student") as? Student
            val position = result.data?.getIntExtra("student_position", -1) ?: -1

            if (updatedStudent != null && position != -1) {
                // Cập nhật dữ liệu trong danh sách gốc
                studentList[position] = updatedStudent
                // Cập nhật giao diện tại vị trí đó
                studentAdapter.notifyItemChanged(position)
                Toast.makeText(this, "Đã cập nhật: ${updatedStudent.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recyclerView)

        // Cài đặt RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            studentList,
            onStudentClick = { student, position ->
                // Sự kiện khi bấm vào một sinh viên -> Mở Activity chi tiết
                openDetailActivity(student, position)
            },
            onDeleteClick = { position ->
                // Sự kiện khi bấm nút xóa
                deleteStudent(position)
            }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // --- PHẦN MENU (QUAN TRỌNG ĐỂ HIỆN NÚT THÊM) ---

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Tạo một item menu có tên "Thêm sinh viên"
        val menuItem = menu?.add(Menu.NONE, 1, Menu.NONE, "Thêm sinh viên")

        // Dòng này giúp đưa nút ra ngoài thanh Header nếu còn chỗ (thay vì ẩn trong 3 chấm)
        menuItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                // Khi nhấn vào nút "Thêm sinh viên", mở màn hình thêm
                openAddStudentActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --- CÁC HÀM CHUYỂN MÀN HÌNH ---

    private fun openAddStudentActivity() {
        val intent = Intent(this, AddStudentActivity::class.java)
        addStudentLauncher.launch(intent)
    }

    private fun openDetailActivity(student: Student, position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        // Truyền dữ liệu sinh viên và vị trí sang màn hình chi tiết
        intent.putExtra("selected_student", student)
        intent.putExtra("student_position", position)
        detailStudentLauncher.launch(intent)
    }

    // --- HÀM XỬ LÝ LOGIC ---

    private fun deleteStudent(position: Int) {
        val studentName = studentList[position].name
        studentList.removeAt(position)

        // Xóa item khỏi giao diện
        studentAdapter.notifyItemRemoved(position)
        // Cập nhật lại index cho các phần tử phía sau để tránh lỗi vị trí
        studentAdapter.notifyItemRangeChanged(position, studentList.size)

        Toast.makeText(this, "Đã xóa: $studentName", Toast.LENGTH_SHORT).show()
    }
}
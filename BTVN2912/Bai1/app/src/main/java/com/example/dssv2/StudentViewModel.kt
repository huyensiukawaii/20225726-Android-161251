package com.example.dssv2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Chuyển từ ViewModel sang AndroidViewModel để lấy Application Context
class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = StudentDatabaseHelper(application)

    // Danh sách sinh viên
    private val _studentList = MutableLiveData<MutableList<Student>>()
    val studentList: LiveData<MutableList<Student>> = _studentList

    // Sinh viên đang được chọn để sửa
    private val _selectedStudent = MutableLiveData<Student?>()
    val selectedStudent: LiveData<Student?> = _selectedStudent

    // Vị trí của sinh viên đang được chọn (Dùng để UI highlight nếu cần)
    private val _selectedPosition = MutableLiveData<Int>(-1)
    val selectedPosition: LiveData<Int> = _selectedPosition

    init {
        // Load dữ liệu từ DB ngay khi khởi tạo ViewModel
        loadStudents()
    }

    // Hàm helper để tải lại dữ liệu từ DB
    private fun loadStudents() {
        _studentList.value = dbHelper.getAllStudents()
    }

    // Thêm sinh viên mới
    fun addStudent(student: Student) {
        val result = dbHelper.addStudent(student)
        if (result != -1L) {
            // Nếu thêm thành công vào DB thì reload lại LiveData
            loadStudents()
        }
    }

    // Cập nhật sinh viên
    // Lưu ý: Logic cũ dùng position để update list, nhưng với SQL ta update theo đối tượng (MSSV)
    fun updateStudent(position: Int, student: Student) {
        val result = dbHelper.updateStudent(student)
        if (result > 0) {
            loadStudents()
        }
    }

    // Xóa sinh viên
    fun deleteStudent(position: Int) {
        // Lấy ra sinh viên tại vị trí đó để lấy MSSV
        val currentList = _studentList.value
        if (currentList != null && position in currentList.indices) {
            val studentToDelete = currentList[position]
            val result = dbHelper.deleteStudent(studentToDelete.mssv)
            if (result > 0) {
                loadStudents()
                // Nếu đang chọn sinh viên vừa bị xóa thì bỏ chọn
                if (_selectedPosition.value == position) {
                    clearSelection()
                }
            }
        }
    }

    // Chọn sinh viên để sửa (Giữ nguyên logic cũ)
    fun selectStudent(student: Student, position: Int) {
        _selectedStudent.value = student
        _selectedPosition.value = position
    }

    // Xóa selection (Giữ nguyên logic cũ)
    fun clearSelection() {
        _selectedStudent.value = null
        _selectedPosition.value = -1
    }
}
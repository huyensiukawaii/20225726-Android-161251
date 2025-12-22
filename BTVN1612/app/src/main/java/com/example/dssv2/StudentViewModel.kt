package com.example.dssv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentViewModel : ViewModel() {

    // Danh sách sinh viên
    private val _studentList = MutableLiveData<MutableList<Student>>(mutableListOf(
        Student("20200001", "Nguyễn Văn A"),
        Student("20200002", "Trần Thị B")
    ))
    val studentList: LiveData<MutableList<Student>> = _studentList

    // Sinh viên đang được chọn để sửa
    private val _selectedStudent = MutableLiveData<Student?>()
    val selectedStudent: LiveData<Student?> = _selectedStudent

    // Vị trí của sinh viên đang được chọn
    private val _selectedPosition = MutableLiveData<Int>(-1)
    val selectedPosition: LiveData<Int> = _selectedPosition

    // Thêm sinh viên mới
    fun addStudent(student: Student) {
        _studentList.value?.add(student)
        _studentList.value = _studentList.value
    }

    // Cập nhật sinh viên
    fun updateStudent(position: Int, student: Student) {
        _studentList.value?.let { list ->
            if (position in list.indices) {
                list[position] = student
                _studentList.value = list
            }
        }
    }

    // Xóa sinh viên
    fun deleteStudent(position: Int) {
        _studentList.value?.let { list ->
            if (position in list.indices) {
                list.removeAt(position)
                _studentList.value = list
            }
        }
    }

    // Chọn sinh viên để sửa
    fun selectStudent(student: Student, position: Int) {
        _selectedStudent.value = student
        _selectedPosition.value = position
    }

    // Xóa selection
    fun clearSelection() {
        _selectedStudent.value = null
        _selectedPosition.value = -1
    }
}
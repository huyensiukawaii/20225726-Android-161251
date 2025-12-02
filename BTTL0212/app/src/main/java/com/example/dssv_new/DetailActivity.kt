// File: DetailActivity.kt

package com.example.dssv_new

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        // Lấy thông tin sinh viên được truyền từ MainActivity
        val student = intent.getSerializableExtra("selected_student") as? Student
        val position = intent.getIntExtra("student_position", -1)

        if (student == null || position == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ánh xạ View
        val edtMssv = findViewById<EditText>(R.id.edtDetailMssv)
        val edtName = findViewById<EditText>(R.id.edtDetailName)
        val edtPhone = findViewById<EditText>(R.id.edtDetailPhone)
        val edtAddress = findViewById<EditText>(R.id.edtDetailAddress)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateStudent)

        // Hiển thị thông tin sinh viên hiện tại
        edtMssv.setText(student.mssv)
        edtName.setText(student.name)
        edtPhone.setText(student.phone)
        edtAddress.setText(student.address)

        btnUpdate.setOnClickListener {
            val name = edtName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()) {
                // Cập nhật đối tượng student
                student.name = name
                student.phone = phone
                student.address = address

                // Tạo Intent và đính kèm đối tượng Student đã cập nhật và vị trí
                val resultIntent = Intent()
                resultIntent.putExtra("updated_student", student)
                resultIntent.putExtra("student_position", position)

                // Thiết lập kết quả thành công và đóng Activity
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Họ tên, SĐT, Địa chỉ không được để trống", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
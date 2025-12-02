// File: AddStudentActivity.kt

package com.example.dssv_new

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddStudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        // Ánh xạ View
        val edtMssv = findViewById<EditText>(R.id.edtAddMssv)
        val edtName = findViewById<EditText>(R.id.edtAddName)
        val edtPhone = findViewById<EditText>(R.id.edtAddPhone)
        val edtAddress = findViewById<EditText>(R.id.edtAddAddress)
        val btnSave = findViewById<Button>(R.id.btnSaveStudent)

        btnSave.setOnClickListener {
            val mssv = edtMssv.text.toString().trim()
            val name = edtName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()

            if (mssv.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()) {
                val newStudent = Student(mssv, name, phone, address)

                // Tạo Intent và đính kèm đối tượng Student
                val resultIntent = Intent()
                resultIntent.putExtra("new_student", newStudent) // Key "new_student"

                // Thiết lập kết quả thành công và đóng Activity
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
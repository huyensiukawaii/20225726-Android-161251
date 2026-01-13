package com.example.internetjson

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Lấy dữ liệu từ Intent
        val student = intent.getSerializableExtra("student_data") as? Student

        if (student != null) {
            findViewById<TextView>(R.id.tvDetailName).text = student.hoten
            findViewById<TextView>(R.id.tvDetailMssv).text = "MSSV: ${student.mssv}"
            findViewById<TextView>(R.id.tvDetailDob).text = "Ngày sinh: ${student.ngaysinh}"
            findViewById<TextView>(R.id.tvDetailEmail).text = "Email: ${student.email}"

            val imgDetail = findViewById<ImageView>(R.id.imgDetail)
            Glide.with(this).load(student.thumbnail).into(imgDetail)
        }
    }
}
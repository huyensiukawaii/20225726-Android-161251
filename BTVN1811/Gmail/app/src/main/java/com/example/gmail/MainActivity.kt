package com.example.gmail

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Tạo dữ liệu giả giống hình ảnh
        val emails = arrayListOf(
            EmailModel("Edurila.com", "$19 Only (First 10 spots)", "Are you looking to Learn Web Designing...", "12:34 PM", Color.parseColor("#4285F4")), // Màu xanh dương
            EmailModel("Chris Abad", "Help make Campaign Monitor better", "Let us know your thoughts! No Images...", "11:22 AM", Color.parseColor("#DB4437")), // Màu đỏ
            EmailModel("Tuto.com", "8h de formation gratuite and les...", "Photoshop, SEO, Blender, CSS, WordPre...", "11:04 AM", Color.parseColor("#0F9D58")), // Màu xanh lá
            EmailModel("support", "Société Ovh : suivi de vos services", "SAS OVH - http://www.ovh.com 2 rue K...", "10:26 AM", Color.parseColor("#607D8B")), // Màu xám
            EmailModel("Matt from Ionic", "The New Ionic Creator Is Here!", "Announcing the all-new Creator, build...", "9:45 AM", Color.parseColor("#FFC107")) // Màu vàng
        )

        val adapter = EmailAdapter(emails)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
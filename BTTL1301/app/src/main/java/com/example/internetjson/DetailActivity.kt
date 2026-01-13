package com.example.internetjson

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Ánh xạ View (Đảm bảo ID khớp 100% với file XML)
        val imgDetail: ImageView = findViewById(R.id.imgDetail)
        val tvName: TextView = findViewById(R.id.tvDetailName)
        val tvMssv: TextView = findViewById(R.id.tvDetailMssv)
        val tvDob: TextView = findViewById(R.id.tvDetailDob)
        val tvEmail: TextView = findViewById(R.id.tvDetailEmail)

        // 2. Lấy dữ liệu an toàn (Hỗ trợ cả Android cũ và mới)
        val student = getStudentFromIntent()

        // 3. Kiểm tra dữ liệu có null không trước khi hiển thị
        if (student != null) {
            tvName.text = student.hoten
            tvMssv.text = "MSSV: ${student.mssv}"
            tvDob.text = "Ngày sinh: ${student.ngaysinh}"
            tvEmail.text = "Email: ${student.email}"

            // Load ảnh
            Glide.with(this)
                .load(student.thumbnail)
                .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh chờ
                .error(android.R.drawable.stat_notify_error)     // Ảnh lỗi
                .into(imgDetail)
        } else {
            // Nếu không lấy được dữ liệu -> Thông báo lỗi
            Toast.makeText(this, "Không thể tải thông tin chi tiết!", Toast.LENGTH_SHORT).show()
            finish() // Đóng Activity quay về màn hình trước
        }
    }

    // Hàm tiện ích để lấy object Serializable an toàn theo phiên bản Android
    private fun getStudentFromIntent(): Student? {
        val key = "student_data"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Dành cho Android 13 trở lên (API 33+)
            intent.getSerializableExtra(key, Student::class.java)
        } else {
            // Dành cho Android cũ hơn
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? Student
        }
    }
}
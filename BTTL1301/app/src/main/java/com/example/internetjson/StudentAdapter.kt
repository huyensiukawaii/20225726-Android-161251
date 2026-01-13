package com.example.internetjson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StudentAdapter(
    private var studentList: List<Student>,
    private val onClick: (Student) -> Unit // Hàm callback khi click
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    // Hàm cập nhật danh sách (dùng cho tính năng tìm kiếm)
    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMssv: TextView = itemView.findViewById(R.id.tvMssv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvName.text = student.hoten
        holder.tvMssv.text = student.mssv

        // Sử dụng Glide để load ảnh
        Glide.with(holder.itemView.context)
            .load(student.thumbnail)
            .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh chờ
            .error(android.R.drawable.stat_notify_error) // Ảnh lỗi
            .into(holder.imgThumbnail)

        // Sự kiện click mở chi tiết
        holder.itemView.setOnClickListener {
            onClick(student)
        }
    }

    override fun getItemCount(): Int = studentList.size
}
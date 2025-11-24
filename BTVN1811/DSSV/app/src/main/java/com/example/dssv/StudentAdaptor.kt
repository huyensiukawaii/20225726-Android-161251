package com.example.dssv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val studentList: MutableList<Student>,
    private val onStudentClick: (Student, Int) -> Unit, // Sự kiện click vào item để sửa
    private val onDeleteClick: (Int) -> Unit            // Sự kiện click vào nút xóa
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMssv: TextView = itemView.findViewById(R.id.tvMssv)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvName.text = student.name
        holder.tvMssv.text = student.mssv

        // Xử lý sự kiện xóa
        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }

        // Xử lý sự kiện chọn item để update
        holder.itemView.setOnClickListener {
            onStudentClick(student, position)
        }
    }

    override fun getItemCount(): Int {
        return studentList.size
    }
}
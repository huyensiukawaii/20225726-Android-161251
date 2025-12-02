package com.example.dssv_new

import android.view.LayoutInflater // Import cho LayoutInflater
import android.view.View         // Import cho View
import android.view.ViewGroup      // Import cho ViewGroup
import android.widget.ImageView    // Import cho ImageView
import android.widget.TextView     // Import cho TextView
import androidx.recyclerview.widget.RecyclerView
class StudentAdapter(
    private val studentList: MutableList<Student>,
    // Thay đổi onStudentClick để chỉ xử lý việc mở Activity, không còn load dữ liệu lên EditText
    private val onStudentClick: (Student, Int) -> Unit, // Sinh viên và Vị trí
    private val onDeleteClick: (Int) -> Unit
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

        // Xử lý sự kiện chọn item để mở DetailActivity
        holder.itemView.setOnClickListener {
            onStudentClick(student, position)
        }
    }

    override fun getItemCount(): Int {
        return studentList.size
    }
}
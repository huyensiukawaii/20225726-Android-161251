package com.example.gmail

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(private val emailList: List<EmailModel>) :
    RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emailList[position]

        holder.tvSender.text = email.sender
        holder.tvTitle.text = email.title
        holder.tvContent.text = email.content
        holder.tvTime.text = email.time

        // Xử lý Avatar: Lấy ký tự đầu tiên
        holder.tvAvatar.text = email.sender.first().toString().uppercase()

        // Đổi màu nền Avatar động
        val background = holder.tvAvatar.background as GradientDrawable
        background.setColor(email.color)
    }

    override fun getItemCount(): Int {
        return emailList.size
    }
}
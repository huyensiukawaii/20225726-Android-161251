package com.example.filemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(
    private var files: List<File>,
    private val onFileClick: (File) -> Unit,
    private val onMoreClick: (File, View) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvFileName)
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val imgMore: ImageView = view.findViewById(R.id.imgMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.tvName.text = file.name

        // Đặt icon tùy theo file hay folder
        if (file.isDirectory) {
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_myplaces) // Icon thư mục
        } else {
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_agenda) // Icon file
        }

        // Nhấn vào item để mở
        holder.itemView.setOnClickListener { onFileClick(file) }
        
        // Nhấn vào nút "more" để mở menu
        holder.imgMore.setOnClickListener { view ->
            onMoreClick(file, view)
        }

        // Bỏ nhấn giữ
        holder.itemView.setOnLongClickListener(null)
    }

    override fun getItemCount() = files.size

    fun updateList(newFiles: List<File>) {
        files = newFiles
        notifyDataSetChanged()
    }
}
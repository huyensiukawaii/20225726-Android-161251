package com.example.playstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val items: List<HomeItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Định nghĩa các ViewHolder khác nhau
    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    }

    inner class VerticalAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvAppName)
        val tvCategory: TextView = view.findViewById(R.id.tvAppCategory)
        val tvRating: TextView = view.findViewById(R.id.tvAppRating)
    }

    inner class HorizontalSectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.rvHorizontal)
    }

    // Hàm xác định loại View dựa trên field 'type' của data
    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HomeItem.TYPE_TITLE -> TitleViewHolder(inflater.inflate(R.layout.item_title, parent, false))
            HomeItem.TYPE_VERTICAL_APP -> VerticalAppViewHolder(inflater.inflate(R.layout.item_app_vertical, parent, false))
            HomeItem.TYPE_HORIZONTAL_SECTION -> HorizontalSectionViewHolder(inflater.inflate(R.layout.item_horizontal_container, parent, false))
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is TitleViewHolder -> {
                holder.tvTitle.text = item.title
            }
            is VerticalAppViewHolder -> {
                holder.tvName.text = item.appData?.name
                holder.tvCategory.text = item.appData?.category
                holder.tvRating.text = item.appData?.rating
            }
            is HorizontalSectionViewHolder -> {
                // Tại đây chúng ta setup RecyclerView con nằm bên trong item
                val adapter = HorizontalAdapter(item.horizontalList ?: emptyList())
                holder.recyclerView.layoutManager =
                    LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
                holder.recyclerView.adapter = adapter
            }
        }
    }

    override fun getItemCount() = items.size
}
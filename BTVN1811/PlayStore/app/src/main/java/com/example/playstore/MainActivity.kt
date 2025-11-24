package com.example.playstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvMain: RecyclerView = findViewById(R.id.rvMain)

        // 1. Tạo dữ liệu mẫu
        val dataList = mutableListOf<HomeItem>()

        // -- Phần 1: Tiêu đề + Danh sách dọc (Suggested for you) --
        dataList.add(HomeItem(HomeItem.TYPE_TITLE, title = "Sponsored • Suggested for you"))

        dataList.add(HomeItem(HomeItem.TYPE_VERTICAL_APP, appData = AppModel("Mech Assemble", "Action • Role Playing", "4.8 ★", 0)))
        dataList.add(HomeItem(HomeItem.TYPE_VERTICAL_APP, appData = AppModel("MU: Hồng Hoả Đạo", "Role Playing", "4.8 ★", 0)))
        dataList.add(HomeItem(HomeItem.TYPE_VERTICAL_APP, appData = AppModel("War Inc: Rising", "Strategy • Tower Defense", "4.9 ★", 0)))

        // -- Phần 2: Tiêu đề + Danh sách ngang (Recommended for you) --
        dataList.add(HomeItem(HomeItem.TYPE_TITLE, title = "Recommended for you"))

        // Tạo danh sách con cho phần cuộn ngang
        val horizontalApps = listOf(
            AppModel("Suno - AI Music", "", "", 0),
            AppModel("Claude by AI", "", "", 0),
            AppModel("DramaBox", "", "", 0),
            AppModel("Pili App", "", "", 0),
            AppModel("TikTok", "", "", 0)
        )
        // Nhét danh sách con vào item loại ngang
        dataList.add(HomeItem(HomeItem.TYPE_HORIZONTAL_SECTION, horizontalList = horizontalApps))

        // 2. Setup Adapter
        val adapter = MainAdapter(dataList)
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapter
    }
}
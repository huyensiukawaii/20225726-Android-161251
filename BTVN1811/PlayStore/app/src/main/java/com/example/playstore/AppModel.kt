package com.example.playstore

// Dùng chung cho cả app hiển thị dọc và ngang
data class AppModel(
    val name: String,
    val category: String,
    val rating: String,
    val imageRes: Int // Lưu ID ảnh (ví dụ: R.drawable.ic_launcher_foreground)
)
// Class wrapper để Adapter biết dòng này hiển thị kiểu gì
data class HomeItem(
    val type: Int, // 1: Title, 2: Vertical App, 3: Horizontal List Container
    val title: String? = null,
    val appData: AppModel? = null,
    val horizontalList: List<AppModel>? = null
) {
    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_VERTICAL_APP = 2
        const val TYPE_HORIZONTAL_SECTION = 3
    }
}
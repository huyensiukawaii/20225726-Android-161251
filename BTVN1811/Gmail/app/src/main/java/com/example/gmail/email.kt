package com.example.gmail

data class EmailModel(
    val sender: String,
    val title: String,
    val content: String,
    val time: String,
    val color: Int
)
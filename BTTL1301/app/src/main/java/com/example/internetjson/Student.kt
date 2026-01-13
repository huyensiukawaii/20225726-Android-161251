package com.example.internetjson
import java.io.Serializable

data class Student(
    val hoten: String,
    val mssv: String,
    val thumbnail: String,
    val ngaysinh: String,
    val email: String
) : Serializable
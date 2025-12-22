package com.example.dssv2
import java.io.Serializable

// Class Student cần implement Serializable hoặc Parcelable để truyền qua Intent
data class Student(
    var mssv: String,
    var name: String,
    var phone: String = "",
    var address: String = ""
) : Serializable
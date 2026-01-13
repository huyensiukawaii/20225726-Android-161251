package com.example.internetjson

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("students") // Endpoint lấy dữ liệu
    fun getStudents(): Call<List<Student>>
}

object RetrofitClient {
    private const val BASE_URL = "https://lebavui.io.vn/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
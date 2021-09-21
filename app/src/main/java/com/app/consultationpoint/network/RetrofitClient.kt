package com.app.consultationpoint.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://postalpincode.in/api/pincode/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getClient(): APIService {
        return retrofit.create(APIService::class.java)
    }
}
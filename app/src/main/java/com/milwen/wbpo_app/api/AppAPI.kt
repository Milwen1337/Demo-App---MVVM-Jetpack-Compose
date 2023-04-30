package com.milwen.wbpo_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppAPI {

    private const val baseUrl = "https://reqres.in/api/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
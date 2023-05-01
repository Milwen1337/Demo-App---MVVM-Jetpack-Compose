package com.milwen.wbpo_app.api

import com.milwen.wbpo_app.registration.model.User
import com.milwen.wbpo_app.registration.model.UserRegisterData
import com.milwen.wbpo_app.registration.model.UserRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiRegisterUser {
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json;charset=utf-8"
    )
    @POST("register/")
    suspend fun registerUser(@Body userData: UserRegisterData) : Response<User>
}
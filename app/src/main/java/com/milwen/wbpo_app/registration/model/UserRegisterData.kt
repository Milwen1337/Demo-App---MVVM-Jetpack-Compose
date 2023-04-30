package com.milwen.wbpo_app.registration.model

import com.google.gson.annotations.SerializedName

data class UserRegisterData(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
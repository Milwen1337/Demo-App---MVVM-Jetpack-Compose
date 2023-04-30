package com.milwen.wbpo_app.userlist.model

data class LoadedUser(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String
)
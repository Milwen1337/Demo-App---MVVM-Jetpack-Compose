package com.milwen.wbpo_app.userlist.model

data class ResponseUsers(
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val data: List<LoadedUser>,
    val support: UserListSupport
)
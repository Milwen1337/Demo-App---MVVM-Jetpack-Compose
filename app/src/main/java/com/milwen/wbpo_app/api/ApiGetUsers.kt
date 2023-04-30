package com.milwen.wbpo_app.api

import com.milwen.wbpo_app.userlist.model.ResponseUsers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiGetUsers {
    @GET("users")
    suspend fun getUsers(@Query("page")page: Int, @Query("per_page")perPage: Int) : Response<ResponseUsers>
}
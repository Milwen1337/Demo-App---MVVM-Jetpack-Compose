package com.milwen.wbpo_app.userlist.viewmodel

import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiGetUsers
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.userlist.model.LoadedUser

class UserListViewModel: MainViewModel(){
    private val repository = AppAPI.getInstance().create(ApiGetUsers::class.java)
    private var users = mutableListOf<LoadedUser>()

    init {
        App.log("UserListViewModel: init")
        loadUsers(1, 5)
    }

    private fun loadUsers(page: Int, perPage: Int){
        App.log("UserListViewModel: loadUsers")
        apiCall(
            { repository.getUsers(page, perPage) },
            onError = { err->
                App.log("UserListViewModel: loadUsers: response error: ${err.apiCallError?.error}")
            },
            onSuccess = { success ->
                App.log("UserListViewModel: loadUsers: response success: ${success.data.toString()}")
                success.data?.data?.let { newUsers->
                    users.apply {
                        clear()
                        addAll(newUsers)
                    }
                }
                App.log("UserListViewModel: loadUsers: response success: numOfUsers: ${users.size}")
            }
        )
    }

}
package com.milwen.wbpo_app.userlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiGetUsers
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.userlist.model.FollowedUser
import com.milwen.wbpo_app.userlist.model.LoadedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserListViewModel(val app: App): MainViewModel(){
    private val repository = AppAPI.getInstance().create(ApiGetUsers::class.java)
    private val db = app.database

    // LiveData holding loaded user list and cached followed user list -> used in View
    private val _users = MutableLiveData<Pair<List<LoadedUser>, List<FollowedUser>>>()
    val users: LiveData<Pair<List<LoadedUser>, List<FollowedUser>>>
        get() = _users

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _areDataLoading = MutableLiveData(true)
    val areDataLoading: LiveData<Boolean> = _areDataLoading


    init {
        App.log("UserListViewModel: init")
        loadUsers(1, 5)
    }

    fun loadAgain(){
        loadUsers(1, 5)
    }

    private fun loadUsers(page: Int, perPage: Int){
        App.log("UserListViewModel: loadUsers")
        _areDataLoading.postValue(true)
        apiCall(
            { repository.getUsers(page, perPage) },
            onError = { err->
                err.apiCallError?.error?.let { e-> _toastMessage.postValue(e) }
                _areDataLoading.postValue(false)
                App.log("UserListViewModel: loadUsers: response error: ${err.apiCallError?.error}")
            },
            onSuccess = { success ->
                App.log("UserListViewModel: loadUsers: response success: ${success.data.toString()}")
                success.data?.data?.let { newUsers->
                    val currentUsers = users.value?.first.orEmpty().toMutableList()
                    val followedUsers = withContext(Dispatchers.IO) {
                        db?.followedUsers()?.getFollowedUsers().orEmpty()
                    }
                    currentUsers.apply {
                        clear()
                        addAll(newUsers)
                    }
                    App.log("UserListViewModel: loadUsers: response success: currentUsers: ${currentUsers.size}")
                    App.log("UserListViewModel: loadUsers: response success: followedUsers: ${followedUsers.size}")
                    _users.value = Pair(currentUsers, followedUsers)
                }
                _areDataLoading.postValue(false)
            }
        )
    }

}
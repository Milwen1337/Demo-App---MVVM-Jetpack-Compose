package com.milwen.wbpo_app.userlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiGetUsers
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.userlist.model.FollowedUser
import com.milwen.wbpo_app.userlist.model.LoadedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserPayload(val users: List<LoadedUser>, val followedUsers: List<FollowedUser>, val fullyLoaded: Boolean = false)
class UserListViewModel(val app: App): MainViewModel(){
    companion object{
        const val DEFAULT_USER_COUNT = 5
    }

    private val repository = AppAPI.getInstance().create(ApiGetUsers::class.java)
    private val db = app.database

    // LiveData holding loaded user list and cached followed user list -> used in View
    private val _users = MutableLiveData<UserPayload>()
    val users: LiveData<UserPayload>
        get() = _users

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

   private var loadAdditinalUsers = false

    private val _areDataLoading = MutableLiveData(true)
    val areDataLoading: LiveData<Boolean> = _areDataLoading

    private val _maybeLoadAgain = MutableLiveData(false)
    val maybeLoadAgain: LiveData<Boolean> = _maybeLoadAgain

    init {
        App.log("UserListViewModel: init")
        loadUsers(true, 1, DEFAULT_USER_COUNT)
    }

    fun loadAgain(){
        loadUsers(true, 1, DEFAULT_USER_COUNT)
    }

    fun changeFollowState(user: LoadedUser){
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.IO){
                db?.let { db->
                    val foundUser = db.followedUsers().getFollowedUserOrNull(user.id)
                    foundUser?.let { fu->
                        db.followedUsers().deleteFollowedUser(fu)
                    }?:kotlin.run { db.followedUsers().addFollowedUser(FollowedUser(user.id)) }

                    val followedUsers = withContext(Dispatchers.IO) {
                        db.followedUsers().getFollowedUsers().orEmpty()
                    }
                    withContext(Dispatchers.Main){
                        refreshData(followedUsers)
                    }
                }
            }
        }
    }

    private fun refreshData(followedUsers: List<FollowedUser>){
        App.log("UserListViewModel: refreshData: followed: ${followedUsers.size}")
        val currentUsers = users.value?.users.orEmpty().toMutableList()
        App.log("UserListViewModel: refreshData: current: ${currentUsers.size}")
        _users.value = UserPayload(currentUsers, followedUsers, fullyLoaded)
    }

    private var currentPage = 1
    private var totalPages = 1
    private var fullyLoaded = false
    fun loadAdditionalUsers(){
        if (!loadAdditinalUsers) {
            loadAdditinalUsers = true
            val currentUsers = users.value?.users.orEmpty().toMutableList()
            if (currentPage != totalPages){
                loadUsers(false, currentPage, DEFAULT_USER_COUNT){
                    loadAdditinalUsers = false
                    App.log("UserListViewModel: loadAdditionalUsers: onLoaded ${currentUsers.size}")
                }
            } else {
                //no more pages
            }
        }
    }

    private fun loadUsers(loadedFirstTime: Boolean, page: Int, perPage: Int, onLoaded: (()->Unit)? = null){
        App.log("UserListViewModel: loadUsers")
        if (loadedFirstTime) _areDataLoading.postValue(true)
        apiCall(
            { repository.getUsers(page, perPage) },
            onError = { err->
                err.apiCallError?.error?.let { e-> _toastMessage.postValue(e) }
                if (loadedFirstTime) _areDataLoading.postValue(false)
                if (loadedFirstTime) _maybeLoadAgain.postValue(true)
                App.log("UserListViewModel: loadUsers: response error: ${err.apiCallError?.error}")
                onLoaded?.invoke()
            },
            onSuccess = { success ->
                App.log("UserListViewModel: loadUsers: response success: ${success.data.toString()}")
                success.data?.let { userData->
                    totalPages = userData.total_pages
                    incrementPages(userData.page, userData.total_pages)
                    val currentUsers = users.value?.users.orEmpty().toMutableList()
                    val followedUsers = withContext(Dispatchers.IO) {
                        db?.followedUsers()?.getFollowedUsers().orEmpty()
                    }
                    currentUsers.apply {
                        addAll(userData.data)
                    }
                    App.log("UserListViewModel: loadUsers: response success: currentUsers: ${currentUsers.size}")
                    App.log("UserListViewModel: loadUsers: response success: followedUsers: ${followedUsers.size}")
                    if (loadedFirstTime) _maybeLoadAgain.postValue(false)
                    fullyLoaded = currentPage == totalPages
                    _users.value = UserPayload(currentUsers, followedUsers, fullyLoaded)
                    onLoaded?.invoke()
                }?:kotlin.run {
                    App.log("UserListViewModel: loadUsers: response success: currentUsers: null")
                    if (loadedFirstTime) _maybeLoadAgain.postValue(false)
                    onLoaded?.invoke()
                }
                if (loadedFirstTime) _areDataLoading.postValue(false)
            }
        )
    }

    private fun incrementPages(current: Int, total: Int){
        if (current < total) currentPage++
    }

}
package com.milwen.wbpo_app.userlist.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiGetUsers
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.database.AppDatabase
import com.milwen.wbpo_app.userlist.model.FollowedUser
import com.milwen.wbpo_app.userlist.model.LoadedUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserListViewModelCompose @Inject constructor(
    private val appDatabase: AppDatabase
): MainViewModel(){
    companion object{
        const val DEFAULT_USER_COUNT = 5
    }

    private val repository = AppAPI.getInstance().create(ApiGetUsers::class.java)

    // LiveData holding loaded user list and cached followed user list -> used in View
    var users = mutableStateOf(UserPayload(emptyList(), emptyList()))

    private var loadAdditinalUsers = false

    var toastMessage = mutableStateOf<String>("")

    var areDataLoading = mutableStateOf(true)

    var maybeLoadAgain = mutableStateOf(false)

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
                appDatabase.let { db->
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
        users.value = UserPayload(currentUsers, followedUsers, fullyLoaded)
    }

    private var currentPage = 1
    private var totalPages = 1
    private var fullyLoaded = false
    fun loadAdditionalUsers(){
        if (!loadAdditinalUsers) {
            loadAdditinalUsers = true
            val currentUsers = users.value.users.toMutableList()
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
        if (loadedFirstTime) areDataLoading.value = true
        apiCall(
            { repository.getUsers(page, perPage) },
            onError = { err->
                err.apiCallError?.error?.let { e-> toastMessage.value = e }
                if (loadedFirstTime) areDataLoading.value = false
                if (loadedFirstTime) maybeLoadAgain.value = true
                App.log("UserListViewModel: loadUsers: response error: ${err.apiCallError?.error}")
                onLoaded?.invoke()
            },
            onSuccess = { success ->
                App.log("UserListViewModel: loadUsers: response success: ${success.data.toString()}")
                success.data?.let { userData->
                    totalPages = userData.total_pages
                    incrementPages(userData.page, userData.total_pages)
                    val currentUsers = users.value.users.toMutableList()
                    val followedUsers = withContext(Dispatchers.IO) {
                        appDatabase.followedUsers().getFollowedUsers().orEmpty()
                    }
                    currentUsers.apply {
                        addAll(userData.data)
                    }
                    App.log("UserListViewModel: loadUsers: response success: currentUsers: ${currentUsers.size}")
                    App.log("UserListViewModel: loadUsers: response success: followedUsers: ${followedUsers.size}")
                    if (loadedFirstTime) maybeLoadAgain.value = false
                    fullyLoaded = currentPage == totalPages
                    users.value = UserPayload(currentUsers, followedUsers, fullyLoaded)
                    onLoaded?.invoke()
                }?:kotlin.run {
                    App.log("UserListViewModel: loadUsers: response success: currentUsers: null")
                    if (loadedFirstTime) maybeLoadAgain.value = false
                    onLoaded?.invoke()
                }
                if (loadedFirstTime) areDataLoading.value = false
            }
        )
    }

    private fun incrementPages(current: Int, total: Int){
        if (current < total) currentPage++
    }

}
package com.milwen.wbpo_app.splash.viewmodel

import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appDatabase: AppDatabase
): MainViewModel() {

    sealed class SplashViewState {
        object Loading : SplashViewState()
        object UserRegistered : SplashViewState()
        object UserNotRegistered : SplashViewState()
        data class Error(val message: String) : SplashViewState()
    }

    val viewState: Flow<SplashViewState> = flow {
        emit(SplashViewState.Loading)

        // Determine user registration status
        val user = appDatabase.userDao().getUser()
        if (user != null) {
            App.log("SplashFragment: navigate to UserList(ViewModel)")
            emit(SplashViewState.UserRegistered)
        } else {
            App.log("SplashFragment: navigate to Registration(ViewModel)")
            emit(SplashViewState.UserNotRegistered)
        }
    }.flowOn(Dispatchers.IO)

}
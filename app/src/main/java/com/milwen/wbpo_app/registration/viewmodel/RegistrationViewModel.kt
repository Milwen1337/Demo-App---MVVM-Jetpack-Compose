package com.milwen.wbpo_app.registration.viewmodel

import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiRegisterUser
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.registration.model.UserRegisterData

class RegistrationViewModel: MainViewModel() {
    private val repository = AppAPI.getInstance().create(ApiRegisterUser::class.java)
    private var userEmail: String? = null
    private var userPassword: String? = null

    init {
        App.log("RegistrationViewModel: init")
        userEmail = "test.email@somemail.com"
        userPassword = "test123"
        registerUser()
    }

    private fun registerUser(){
        App.log("RegistrationViewModel: registerUser")
        val email = userEmail
        val password = userPassword
        if (email != null && password != null){
            apiCall(
                { repository.registerUser(UserRegisterData(email, password)) },
                onError = { err->
                    App.log("RegistrationViewModel: registerUser: response error: ${err.apiCallError?.error}")
                },
                onSuccess = { success ->
                    App.log("RegistrationViewModel: registerUser: response success: ${success.data.toString()}")
                }
            )
        }
    }

}
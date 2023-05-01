package com.milwen.wbpo_app.registration.viewmodel

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiRegisterUser
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.isEmailValid
import com.milwen.wbpo_app.onDoubleTouchProtectClick
import com.milwen.wbpo_app.registration.model.RegisterData
import com.milwen.wbpo_app.registration.model.UserRegisterData

class RegistrationViewModel: MainViewModel() {
    private val repository = AppAPI.getInstance().create(ApiRegisterUser::class.java)

    val registerData = RegisterData()
    var userEmailCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            validateData()
        }
    }

    var userPasswordCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            validateData()
        }
    }

    @BindingAdapter("onDoubleTouchProtectClick")
    fun View.setOnDoubleTouchProtectClickListener(clickListener: View.OnClickListener) {
        setOnClickListener {
            this.onDoubleTouchProtectClick {
                clickListener.onClick(this)
            }
        }
    }

    private val _finishRegistration = MutableLiveData(false)
    val finishRegistration: LiveData<Boolean> = _finishRegistration

    private val _isRegButtonEnabled = MutableLiveData(true)
    val isRegButtonEnabled: LiveData<Boolean> = _isRegButtonEnabled

    private val _isDataValid = MutableLiveData(false)
    val isDataValid: LiveData<Boolean> = _isDataValid

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    init {
        App.log("RegistrationViewModel: init")
        //userEmail = "eve.holt@reqres.in"
        //userPassword = "pistol"

        initDataObservables()
    }

    private fun validateData(){
        val email = registerData.email
        val password = registerData.password
        _isDataValid.postValue(!password.isNullOrBlank() && !email.isNullOrBlank() && isEmailValid(email))
    }

    private fun initDataObservables(){
        registerData.apply {
            addOnPropertyChangedCallback(userEmailCallback)
            addOnPropertyChangedCallback(userPasswordCallback)
        }
    }

    fun onRegister() {
        _isRegButtonEnabled.postValue(false)
        registerUser()
    }

    private fun registerUser(){
        App.log("RegistrationViewModel: registerUser")
        val email = registerData.email
        val password = registerData.password
        if (email != null && password != null){
            apiCall(
                { repository.registerUser(UserRegisterData(email, password)) },
                onError = { err->
                    _isRegButtonEnabled.postValue(true)
                    err.apiCallError?.error?.let { e-> _toastMessage.postValue(e) }
                    App.log("RegistrationViewModel: registerUser: response error: ${err.apiCallError?.error}")
                },
                onSuccess = { success ->
                    _isRegButtonEnabled.postValue(true)
                    _finishRegistration.postValue(true)
                    App.log("RegistrationViewModel: registerUser: response success: ${success.data.toString()}")
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        registerData.apply {
            removeOnPropertyChangedCallback(userEmailCallback)
            removeOnPropertyChangedCallback(userPasswordCallback)
        }
    }

}
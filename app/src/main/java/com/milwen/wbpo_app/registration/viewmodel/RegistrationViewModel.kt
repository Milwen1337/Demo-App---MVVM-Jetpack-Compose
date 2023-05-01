package com.milwen.wbpo_app.registration.viewmodel

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiRegisterUser
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.isEmailValid
import com.milwen.wbpo_app.onDoubleTouchProtectClick
import com.milwen.wbpo_app.registration.model.User
import com.milwen.wbpo_app.registration.model.UserRegisterData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrationViewModel(val app: App): MainViewModel() {
    private val repository = AppAPI.getInstance().create(ApiRegisterUser::class.java)
    private val db = app.database

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private val emailObserver = Observer<String> { validateData() }
    private val passwordObserver = Observer<String> { validateData() }

    companion object {
        @BindingAdapter("onDoubleTouchProtectClick")
        @JvmStatic
        fun View.setOnDoubleTouchProtectClickListener(clickListener: View.OnClickListener) {
            setOnClickListener {
                this.onDoubleTouchProtectClick {
                    clickListener.onClick(this)
                }
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
        initFieldObservers()
    }

    private fun initFieldObservers(){
        email.observeForever(emailObserver)
        password.observeForever(passwordObserver)
    }

    private fun validateData(){
        App.log("RegistrationViewModel: validateData")
        val mEmail: String? = email.value
        val mPassword: String? = password.value
        App.log("RegistrationViewModel: validateData: $mEmail, $mPassword")
        _isDataValid.postValue(!mPassword.isNullOrBlank() && !mEmail.isNullOrBlank() && isEmailValid(mEmail))
    }

    fun onRegister() {
        _isRegButtonEnabled.postValue(false)
        registerUser()
    }

    private fun registerUser(){
        App.log("RegistrationViewModel: registerUser")
        val email = email.value
        val password = password.value
        if (email != null && password != null){
            apiCall(
                { repository.registerUser(UserRegisterData(email, password)) },
                onError = { err->
                    _isRegButtonEnabled.postValue(true)
                    err.apiCallError?.error?.let { e-> _toastMessage.postValue(e) }
                    App.log("RegistrationViewModel: registerUser: response error: ${err.apiCallError?.error}")
                },
                onSuccess = { success ->
                    App.log("RegistrationViewModel: registerUser: response success: ${success.data.toString()}")
                    withContext(Dispatchers.IO){
                        success.data?.let { user->
                            db?.let { db->
                                App.log("RegistrationViewModel: registerUser: response success: insertUser")
                                db.userDao().insertUser(user)
                            }
                        }
                    }
                    _isRegButtonEnabled.postValue(true)
                    _finishRegistration.postValue(true)
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        email.removeObserver(emailObserver)
        password.removeObserver(passwordObserver)
    }

}
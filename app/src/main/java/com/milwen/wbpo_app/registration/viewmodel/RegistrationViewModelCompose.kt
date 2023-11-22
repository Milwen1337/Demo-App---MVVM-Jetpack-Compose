package com.milwen.wbpo_app.registration.viewmodel

import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.milwen.wbpo_app.MainViewModel
import com.milwen.wbpo_app.api.ApiRegisterUser
import com.milwen.wbpo_app.api.AppAPI
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.database.AppDatabase
import com.milwen.wbpo_app.isEmailValid
import com.milwen.wbpo_app.onDoubleTouchProtectClick
import com.milwen.wbpo_app.registration.model.UserRegisterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModelCompose @Inject constructor(
    private val appDatabase: AppDatabase
): MainViewModel() {
    private val repository = AppAPI.getInstance().create(ApiRegisterUser::class.java)

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private val emailObserver = Observer<String> { validateData() }
    private val passwordObserver = Observer<String> { validateData() }

    companion object {
        @BindingAdapter("onDoubleTouchProtectClick")
        @JvmStatic
        fun View.setOnDoubleTouchProtectClickListener(clickListener: View.OnClickListener) {
            this.onDoubleTouchProtectClick {
                clickListener.onClick(this)
            }
        }
    }

    private val _finishRegistration = mutableStateOf(false)
    val finishRegistration: State<Boolean> = _finishRegistration

    private val _isRegButtonEnabled = mutableStateOf(true)
    val isRegButtonEnabled: State<Boolean> = _isRegButtonEnabled

    private val _isDataValid = mutableStateOf(false)
    val isDataValid: State<Boolean> = _isDataValid

    private val _toastMessage = mutableStateOf("")
    val toastMessage: State<String> = _toastMessage


    /**
     * For testing based on API documentation you have to type these credentials for successful registration
     * email = eve.holt@reqres.in
     * password = pistol
     */
    init {
        App.log("RegistrationViewModel: init")
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
        _isDataValid.value = (!mPassword.isNullOrBlank() && !mEmail.isNullOrBlank() && isEmailValid(mEmail))
    }

    fun onRegister() {
        _isRegButtonEnabled.value = (false)
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
                    _isRegButtonEnabled.value = (true)
                    err.apiCallError?.error?.let { e-> _toastMessage.value = (e) }
                    App.log("RegistrationViewModel: registerUser: response error: ${err.apiCallError?.error}")
                },
                onSuccess = { success ->
                    App.log("RegistrationViewModel: registerUser: response success: ${success.data.toString()}")
                    withContext(Dispatchers.IO){
                        success.data?.let { user->
                            App.log("RegistrationViewModel: registerUser: response success: insertUser")
                            appDatabase.userDao().insertUser(user)
                        }
                    }
                    _isRegButtonEnabled.value = (true)
                    _finishRegistration.value = (true)
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
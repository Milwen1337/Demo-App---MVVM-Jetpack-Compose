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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModelCompose @Inject constructor(
    private val appDatabase: AppDatabase
): MainViewModel() {
    private val repository = AppAPI.getInstance().create(ApiRegisterUser::class.java)

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun setEmail(value: String) {
        _email.value = value
        validateData()
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun setPassword(value: String) {
        _password.value = value
        validateData()
    }

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

    private fun validateData(){
        App.log("RegistrationViewModel: validateData")
        val mEmail: String = email.value
        val mPassword: String = password.value
        App.log("RegistrationViewModel: validateData: $mEmail, $mPassword")
        _isDataValid.value = (mPassword.isNotBlank() && mEmail.isNotBlank() && isEmailValid(mEmail))
    }

    fun onRegister() {
        _isRegButtonEnabled.value = (false)
        registerUser()
    }

    private fun registerUser(){
        App.log("RegistrationViewModel: registerUser")
        val email = email.value
        val password = password.value
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
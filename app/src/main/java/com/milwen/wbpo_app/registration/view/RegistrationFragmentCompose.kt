package com.milwen.wbpo_app.registration.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.registration.viewmodel.RegistrationViewModelCompose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModelCompose = hiltViewModel(),
    navController: NavController,
    session: String? = null
) {
    val context = LocalContext.current

    val emailState = rememberUpdatedState(newValue = viewModel.email.value)
    val passwordState = rememberUpdatedState(newValue = viewModel.password.value)
    val errorState = rememberUpdatedState(newValue = viewModel.toastMessage.value)

    // Register Button Click event
    val onRegisterButtonClick: () -> Unit = {
        viewModel.onRegister()
    }

    // UI layout
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = context.getString(R.string.registration_fragment_title)) })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Email Field
                TextField(
                    value = emailState.value?:"",
                    onValueChange = { value -> viewModel.email.value = value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    enabled = viewModel.isRegButtonEnabled.value && viewModel.isDataValid.value
                )

                // Password Field
                TextField(
                    value = passwordState.value?:"",
                    onValueChange = { value -> viewModel.password.value = value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    enabled = viewModel.isRegButtonEnabled.value && viewModel.isDataValid.value
                )

                // Register Button
                Button(
                    onClick = onRegisterButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.isRegButtonEnabled.value && viewModel.isDataValid.value
                ) {
                    Text(text = "Register")
                }
            }
        }
    )

    // Handle toast message
    if (errorState.value.isNotEmpty()) {
        showRegistrationError(LocalContext.current, errorState.value)
    }

    // Handle finishing registration
    if (viewModel.finishRegistration.value) {
        navController.navigate("userListScreen")
    }
}

private fun showRegistrationError(context: Context, err: String){
    App.showToast(context, err)
}

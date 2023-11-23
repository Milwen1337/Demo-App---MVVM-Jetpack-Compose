package com.milwen.wbpo_app.registration.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.milwen.wbpo_app.*
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.registration.viewmodel.RegistrationViewModelCompose

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModelCompose = hiltViewModel(),
    navController: NavController,
    session: String? = null
) {
    val context = LocalContext.current

    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val errorState = rememberUpdatedState(newValue = viewModel.toastMessage.value)

    // Register Button Click event
    val onRegisterButtonClick: () -> Unit = {
        viewModel.onRegister()
    }
    val textColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 1f)
    val textColorFocused = colorFocused.copy(alpha = 1f)

    val focusManager = LocalFocusManager.current

    // UI layout
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = context.getString(R.string.registration_fragment_title)) })
        },
        content = {contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = contentPadding.calculateTopPadding())
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.getString(R.string.registration_info),
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp),
                        color = textColor
                    )
                }

                // Email Field
                OutlinedTextField(
                    value = email.value,
                    onValueChange = viewModel::setEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = viewModel.isRegButtonEnabled.value,
                    label = {
                        Text(
                            text = context.getString(R.string.email_label),
                            color = textColor
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = textColorFocused,
                        unfocusedBorderColor = textColor,
                        cursorColor = textColorFocused
                    ),
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                )

                // Password Field
                OutlinedTextField(
                    value = password.value,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = viewModel::setPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = viewModel.isRegButtonEnabled.value,
                    label = {
                        Text(
                            text = context.getString(R.string.password_label),
                            color = textColor
                        )

                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = textColorFocused,
                        unfocusedBorderColor = textColor,
                        cursorColor = textColorFocused
                    ),
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                )

                // Register Button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = textColorFocused,
                        contentColor = textColor,
                    ),
                    onClick = onRegisterButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    enabled = viewModel.isRegButtonEnabled.value && viewModel.isDataValid.value
                ) {
                    Text(text = context.getString(R.string.button_register), fontSize = 16.sp)
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

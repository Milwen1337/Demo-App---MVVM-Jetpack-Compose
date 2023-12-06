package com.milwen.wbpo_app.registration.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.milwen.wbpo_app.*
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
    val errorState = rememberUpdatedState(newValue = viewModel.toastMessage.value)
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
                welcomeHeader(label = context.getString(R.string.registration_info))
                emailField(
                    viewModel = viewModel,
                    label = context.getString(R.string.email_label),
                    displayError = context.getString(R.string.email_invalid_message)
                )
                passwordField(
                    viewModel = viewModel,
                    label = context.getString(R.string.password_label),
                    displayError = context.getString(R.string.password_invalid_message_1)
                )
                registerButton(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    text = context.getString(R.string.button_register)
                )
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

@Composable
private fun welcomeHeader(label: String){
    val textColor = MaterialTheme.colorScheme.tertiary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            color = textColor
        )
    }
}

@Composable
private fun emailField(
    viewModel: RegistrationViewModelCompose,
    label: String,
    displayError: String
){
    val email = viewModel.email.collectAsState()
    val isEmailValid = viewModel.isEmailValid.value
    val textColor = MaterialTheme.colorScheme.tertiary
    val focusManager = LocalFocusManager.current
    val shouldDisplayError = !isEmailValid && email.value.isNotBlank()
    OutlinedTextField(
        value = email.value,
        onValueChange = viewModel::setEmail,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        enabled = viewModel.isRegButtonEnabled.value,
        label = {
            Text(
                text = label,
                color = textColor
            )
        },
        isError = shouldDisplayError,
        supportingText = {
            if (shouldDisplayError){
                textFieldError(value = displayError)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = colorFocused,
            unfocusedBorderColor = textColor,
            cursorColor = colorFocused
        ),
        maxLines = 1,
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
    )
}

@Composable
private fun passwordField(
    viewModel: RegistrationViewModelCompose,
    label: String,
    displayError: String
){
    val password = viewModel.password.collectAsState()
    val isPasswordValid = viewModel.isPasswordValid.value
    val textColor = MaterialTheme.colorScheme.tertiary
    val focusManager = LocalFocusManager.current
    val shouldDisplayError = !isPasswordValid && password.value.isNotBlank()
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password.value,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        onValueChange = viewModel::setPassword,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        enabled = viewModel.isRegButtonEnabled.value,
        label = {
            Text(
                text = label,
                color = textColor
            )

        },
        trailingIcon = {
            IconButton(
                onClick = {
                    isPasswordVisible = !isPasswordVisible
                }
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = null
                )
            }
        },
        isError = shouldDisplayError,
        supportingText = {
            if (shouldDisplayError){
                textFieldError(value = displayError)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = colorFocused,
            unfocusedBorderColor = textColor,
            cursorColor = colorFocused
        ),
        maxLines = 1,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
    )
}

@Composable
private fun registerButton(
    viewModel: RegistrationViewModelCompose,
    modifier: Modifier,
    text: String
){
    val isButtonEnabled = viewModel.isRegButtonEnabled.value && viewModel.isDataValid.value
    val textColor = if (isButtonEnabled) colorWhite else colorWhite.copy(alpha = 0.4f)
    val onRegisterButtonClick: () -> Unit = {
        viewModel.onRegister()
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFocused,
            contentColor = textColor,
        ),
        onClick = onRegisterButtonClick,
        modifier = modifier,
        enabled = isButtonEnabled
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
private fun textFieldError(value: String){
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = value,
        color = MaterialTheme.colorScheme.error
    )
}


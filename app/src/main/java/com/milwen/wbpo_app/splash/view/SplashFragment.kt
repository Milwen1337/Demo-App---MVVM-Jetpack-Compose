package com.milwen.wbpo_app.splash.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.splash.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController
) {
    val viewState = viewModel.viewState.collectAsState(initial = null).value

    // UI layout
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Field
                Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
                if (viewState is SplashViewModel.SplashViewState.Loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    )

    when (viewState) {
        is SplashViewModel.SplashViewState.UserRegistered -> {
            // Navigate to the user list screen
            App.log("SplashFragment: navigate to UserList")
            val session = "19299384020020934800202388AKDJDIAJISJD"
            navController.navigate("userListScreen")
        }
        is SplashViewModel.SplashViewState.UserNotRegistered -> {
            // Navigate to the registration screen
            App.log("SplashFragment: navigate to Registration")
            navController.navigate("registrationScreen")
        }
        else -> {}
    }
}
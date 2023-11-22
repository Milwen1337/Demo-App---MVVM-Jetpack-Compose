package com.milwen.wbpo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.database.AppDatabase
import com.milwen.wbpo_app.registration.view.RegistrationScreen
import com.milwen.wbpo_app.splash.view.SplashScreen
import com.milwen.wbpo_app.userlist.view.UserListScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivityCompose: ComponentActivity(){

    @Inject
    lateinit var app: App

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(navController = rememberNavController())
        }
    }

    @Composable
    fun MyApp(navController: NavHostController) {
        App.log("Navigation: MainActivity")
        NavHost(
            navController = navController,
            startDestination = "splashScreen"
        ) {
            composable("splashScreen") {
                SplashScreen(navController = navController)
            }
            composable(
                route = "registrationScreen/{session}",
                arguments = listOf(navArgument("session") { type = NavType.StringType })
            ) { backStackEntry ->
                val session = backStackEntry.arguments?.getString("session")
                RegistrationScreen(navController = navController, session = session)
            }
            composable("registrationScreen") {
                App.log("Navigation: SplashScreen -> RegistrationScreen")
                RegistrationScreen(navController = navController)
            }
            composable("userListScreen") {
                App.log("Navigation: SplashScreen -> UserListScreen")
                UserListScreen(navController = navController)
            }
        }
    }
}
package com.example.chat.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.ui.main.MainScreen
import com.example.chat.ui.main.Screen
import com.example.chat.ui.auth.AuthScreen
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import org.koin.androidx.compose.get

@Composable
fun Root() {
    val navController = rememberNavController()

    val getUserUseCase = get<GetUserUseCase>()
    val savedUserId = getUserUseCase()

    val startDestination = if(savedUserId == null) Screen.Auth.route else Screen.Main.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Auth.route) {
            AuthScreen({
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Auth.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Screen.Main.route) {
            MainScreen(navigateToAuth = { navController.navigate(Screen.Auth.route) })
        }
    }
}
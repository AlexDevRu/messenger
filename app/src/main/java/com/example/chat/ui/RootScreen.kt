package com.example.chat.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat.ui.chat.ChannelScreen
import com.example.chat.ui.main.MainScreen
import com.example.chat.ui.main.Screen
import com.example.chat.ui.sign_in.AuthScreen
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import org.koin.androidx.compose.get

const val UserIdArgName = "userId"

@Composable
fun Root() {
    val navController = rememberNavController()

    var start by remember { mutableStateOf(true) }

    val getUserUseCase = get<GetUserUseCase>()
    val savedUserId = getUserUseCase()

    val startDestination = if(savedUserId == null) Screen.Auth.route else Screen.Main.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Auth.route) {
            AuthScreen({ navController.navigate(Screen.Main.route) })
        }
        composable(Screen.Channel.route + "/{channelId}") {
            val channelId = it.arguments?.getString("channelId")
            ChannelScreen(channelId!!)
        }
        composable(Screen.Main.route, arguments = listOf(
            navArgument(UserIdArgName) {
                nullable = true
                defaultValue = null
            }
        )) {
            val userId = if(start) savedUserId else it.arguments?.getString(UserIdArgName)
            MainScreen(userId = userId, rootController = navController, navigateToAuth = { navController.navigate(Screen.Auth.route) })
            start = false
        }
    }
}
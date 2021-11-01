package com.example.chat.ui.models

sealed class Screen(val route: String) {
    object Auth : Screen("Auth")
    object Main : Screen("Main")
    object Channel: Screen("Channel")
    object EditProfile : Screen("EditProfile")
    object Users : Screen("Users")
    object DeleteChannel : Screen("DeleteChannel")
}
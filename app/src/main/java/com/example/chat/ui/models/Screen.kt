package com.example.chat.ui.models

import androidx.annotation.StringRes
import com.example.chat.R

const val channelCidArgName = "cid"
const val userArgName = "user"

sealed class Screen(val route: String, @StringRes val displayText: Int? = null) {
    object Auth : Screen("Auth")
    object Main : Screen("Main", R.string.app_name)
    object Channel: Screen("Channel/{$channelCidArgName}") {
        fun createRoute(cid: String) = "Channel/$cid"
    }
    object UserInfoScreen: Screen("UserInfo")
    object EditProfile : Screen("EditProfile", R.string.edit_profile)
    object Users : Screen("Users", R.string.users)
    object DeleteChannel : Screen("DeleteChannel/{$channelCidArgName}", R.string.delete_channel_dialog_title) {
        fun createRoute(cid: String) = "DeleteChannel/$cid"
    }
}
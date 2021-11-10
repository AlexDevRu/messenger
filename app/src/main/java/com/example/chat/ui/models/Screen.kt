package com.example.chat.ui.models

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.navigation.NavType
import com.example.chat.R
import com.example.domain.models.ChatUser
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.util.*

const val channelCidArgName = "cid"
const val userArgName = "user"

@Parcelize
data class ChatUserArg(
    val id: String = "",
    val email: String = "",
    val userName: String = "",
    val phone: String? = null,
    val avatar: String? = null,
    val lastActive: Date? = null
): Parcelable

fun ChatUser.toArg(): ChatUserArg {
    return ChatUserArg(
        id = id,
        email = email,
        userName = userName,
        phone = phone,
        avatar = avatar.toString(),
        lastActive = lastActive
    )
}

fun ChatUserArg.toDomainModel(): ChatUser {
    return ChatUser(
        id = id,
        email = email,
        userName = userName,
        phone = phone,
        avatar = avatar.toString(),
        lastActive = lastActive
    )
}

class UserParamType : NavType<ChatUserArg>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): ChatUserArg? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): ChatUserArg {
        return Gson().fromJson(value, ChatUserArg::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: ChatUserArg) {
        bundle.putParcelable(key, value)
    }
}

sealed class Screen(val route: String, @StringRes val displayText: Int? = null) {
    object Auth : Screen("Auth")
    object Main : Screen("Main", R.string.app_name)
    object Channel: Screen("Channel/{$channelCidArgName}") {
        fun createRoute(cid: String) = "Channel/$cid"
    }
    object UserInfoScreen: Screen("UserInfo/{$userArgName}") {
        fun createRoute(user: ChatUser): String {
            val json = Uri.encode(Gson().toJson(user.toArg()))
            return "UserInfo/$json"
        }
    }
    object EditProfile : Screen("EditProfile", R.string.edit_profile)
    object Users : Screen("Users", R.string.users)
    object DeleteChannel : Screen("DeleteChannel/{$channelCidArgName}", R.string.delete_channel_dialog_title) {
        fun createRoute(cid: String) = "DeleteChannel/$cid"
    }
}
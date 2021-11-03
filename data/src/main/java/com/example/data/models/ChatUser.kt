package com.example.data.models

import com.example.domain.models.ChatUser

fun ChatUser.getAvatarOrDefault(): String {
    val defaultAvatar = "https://getstream.imgix.net/images/random_svg/${userName.firstOrNull()?.uppercase()}.svg"
    return avatar ?: defaultAvatar
}
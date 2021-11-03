package com.example.data.mappers

import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User

fun User.toDomainModel(): ChatUser {
    return ChatUser(
        id = id,
        email = extraData["email"].toString(),
        userName = name,
        avatar = image
    )
}

fun ChatUser.toDataModel(): User {
    return User(id = id, extraData = mutableMapOf(
        "email" to email,
        "name" to userName,
        "image" to avatar.orEmpty()
    ))
}
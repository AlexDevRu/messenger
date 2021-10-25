package com.example.data.mappers

import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User

fun User.toDomainModel(): ChatUser {
    return ChatUser(id, name)
}

fun ChatUser.toDataModel(): User {
    return User(id = id, extraData = mutableMapOf(
        "name" to name
    ))
}
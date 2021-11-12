package com.example.chat.args.mappers

import com.example.chat.args.models.ChatUserArg
import com.example.domain.models.ChatUser

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
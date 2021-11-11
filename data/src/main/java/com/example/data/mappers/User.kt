package com.example.data.mappers

import com.example.data.database.entities.ChatUserEntity
import com.example.data.database.entities.ChatUserImageEntity
import com.example.data.database.entities.ChatUserPhoneEntity
import com.example.data.database.entities.ChatUserWithMetadata
import com.example.data.extensions.email
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User


fun User.toDomainModel(): ChatUser {
    return ChatUser(
        id = id,
        email = email,
        userName = name,
        avatar = image,
        lastActive = lastActive,
    )
}

fun ChatUser.toDataModel(): User {
    val user = User(id = id)
    user.name = userName
    user.image = if(avatar == null) "" else avatar.toString()
    user.email = email
    user.lastActive = lastActive
    return user
}

fun ChatUserWithMetadata.toDomainModel(): ChatUser {
    return ChatUser(
        id = user.id,
        email = user.email,
        userName = user.userName,
        phone = phone.phone,
        avatar = image.image,
        lastActive = user.lastActive,
    )
}

fun ChatUser.toEntity(): ChatUserWithMetadata {
    return ChatUserWithMetadata(
        user = ChatUserEntity(id = id, userName = userName, email = email, lastActive = lastActive),
        image = ChatUserImageEntity(image = if(avatar == null) "" else avatar.toString(), userId = id),
        phone = ChatUserPhoneEntity(phone = phone, userId = id)
    )
}

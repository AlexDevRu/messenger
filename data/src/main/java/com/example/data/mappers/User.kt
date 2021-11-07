package com.example.data.mappers

import com.example.data.database.entities.ChatUserEntity
import com.example.data.database.entities.ChatUserImageEntity
import com.example.data.database.entities.ChatUserPhoneEntity
import com.example.data.database.entities.ChatUserWithMetadata
import com.example.domain.models.ChatUser
import com.google.firebase.auth.FirebaseUser
import io.getstream.chat.android.client.models.User

fun User.toDomainModel(): ChatUser {
    return ChatUser(
        id = id,
        email = extraData["email"].toString(),
        userName = extraData["name"].toString(),
        avatar = extraData["image"].toString()
    )
}

fun ChatUser.toDataModel(): User {
    val user = User(id = id)
    user.name = userName
    user.image = if(avatar == null) "" else avatar.toString()
    user.extraData["image"] = if(avatar == null) "" else avatar.toString()
    user.extraData["name"] = userName
    user.extraData["email"] = email
    return user
}

fun ChatUserWithMetadata.toDomainModel(): ChatUser {
    return ChatUser(
        id = user.id,
        email = user.email,
        userName = user.userName,
        phone = phone.phone,
        avatar = image.image
    )
}

fun ChatUser.toEntity(): ChatUserWithMetadata {
    return ChatUserWithMetadata(
        user = ChatUserEntity(id = id, userName = userName, email = email),
        image = ChatUserImageEntity(image = if(avatar == null) "" else avatar.toString(), userId = id),
        phone = ChatUserPhoneEntity(phone = phone, userId = id)
    )
}

fun FirebaseUser.toDataModel(): User {
    return User(id = uid, extraData = mutableMapOf(
        "email" to email!!,
        "name" to displayName.orEmpty(),
        "image" to if(photoUrl != null) photoUrl.toString() else ""
    ))
}

fun FirebaseUser.toDomainModel(): ChatUser {
    return ChatUser(
        id = uid,
        email = email!!,
        userName = displayName.orEmpty(),
        avatar = if(photoUrl != null) photoUrl.toString() else ""
    )
}
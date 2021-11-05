package com.example.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ChatUserWithMetadata(
    @Embedded val user: ChatUserEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val image: ChatUserImageEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val phone: ChatUserPhoneEntity
)

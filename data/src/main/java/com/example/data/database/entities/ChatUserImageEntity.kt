package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "users_images",
    foreignKeys = [
        ForeignKey(
            entity = ChatUserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatUserImageEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val image: String? = null,
    val userId: String,
    val pending: Boolean = false
)

package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "users_phones",
    foreignKeys = [
        ForeignKey(
            entity = ChatUserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatUserPhoneEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val phone: String? = null,
    val userId: String,
    val pending: Boolean = false
)

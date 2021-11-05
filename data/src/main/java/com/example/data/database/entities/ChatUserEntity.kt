package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class ChatUserEntity(
    @PrimaryKey val id: String,
    val userName: String,
    val email: String,
    var pending: Boolean = false
)

package com.example.data.database.entities.remote_keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_remote_keys")
data class UserRemoteKeys(
    @PrimaryKey var userId: String,
    var prevKey: Int?,
    var nextKey: Int?
)

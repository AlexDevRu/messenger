package com.example.domain.repositories.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser

interface IStreamChatRepository {
    suspend fun getUserById(userId: String): Result<ChatUser?>
    suspend fun connectUser(userId: String): Result<Unit>
    fun deleteChannel(cid: String)
}
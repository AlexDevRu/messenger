package com.example.domain.repositories.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser

interface IStreamChatRepository {
    suspend fun getUserById(userId: String): ChatUser
    suspend fun connectUser(userId: String, email: String): ChatUser
    suspend fun connectUser(userId: String): ChatUser
    fun deleteChannel(cid: String)
    suspend fun partialUpdateUser(userId: String, name: String, avatar: String): ChatUser
    fun logout()
}
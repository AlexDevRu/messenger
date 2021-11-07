package com.example.domain.repositories.remote

import com.example.domain.models.ChatUser

interface IStreamChatRepository {
    suspend fun getUserById(userId: String): ChatUser
    suspend fun connectUser(userId: String, userName: String, email: String): ChatUser
    suspend fun connectUser(userId: String): ChatUser
    suspend fun updateCurrentUser(userName: String, photoUrl: String): ChatUser
    fun deleteChannel(cid: String)
    fun logout()
    suspend fun getUsersByQuery(query: String): List<ChatUser>
}
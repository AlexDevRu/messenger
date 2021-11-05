package com.example.domain.repositories.local.database

import com.example.domain.models.ChatUser

interface IUsersRepository {
    suspend fun getUsersByQuery(query: String, excludeCurrentUser: Boolean = true): List<ChatUser>
    suspend fun getUserById(id: String): ChatUser?
    suspend fun saveUsers(users: List<ChatUser>)
    suspend fun saveUser(user: ChatUser)
}
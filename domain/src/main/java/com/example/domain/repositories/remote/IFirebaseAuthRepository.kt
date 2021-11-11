package com.example.domain.repositories.remote

import com.example.domain.models.ChatUser

interface IFirebaseAuthRepository {
    suspend fun checkUserCredentials(email: String, password: String): String
    suspend fun createNewUser(email: String, userName: String, password: String): String
    suspend fun updateCurrentUser(name: String, photoUrl: String): ChatUser
    suspend fun linkWithCredentials(verificationId: String, smsCode: String)
    suspend fun linkWithCredentials(credentials: Any)
    fun logout()
}
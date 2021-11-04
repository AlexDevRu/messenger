package com.example.domain.repositories.remote

interface IFirebaseAuthRepository {
    suspend fun checkUserCredentials(email: String, password: String): String
    suspend fun createNewUser(email: String, password: String): String
    suspend fun linkWithCredentials(verificationId: String, smsCode: String)
    suspend fun linkWithCredentials(credentials: Any)
    fun logout()
}
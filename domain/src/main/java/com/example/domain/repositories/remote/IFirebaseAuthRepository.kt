package com.example.domain.repositories.remote

import com.example.domain.common.Result

interface IFirebaseAuthRepository {
    suspend fun checkUserCredentials(email: String, password: String): Result<String>
    suspend fun createNewUser(email: String, password: String): Result<String>
}
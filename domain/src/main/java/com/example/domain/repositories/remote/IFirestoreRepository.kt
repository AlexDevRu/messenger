package com.example.domain.repositories.remote

import com.example.domain.common.Result

interface IFirestoreRepository {
    suspend fun checkUserCredentials(userId: String, password: String): Result<Unit>
    suspend fun createNewUser(userId: String, password: String): Result<Unit>
}
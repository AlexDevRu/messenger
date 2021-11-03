package com.example.domain.repositories.remote

import com.example.domain.common.Result

interface IFirebaseStorageRepository {
    suspend fun saveAvatar(userId: String, avatar: Any): String?
}
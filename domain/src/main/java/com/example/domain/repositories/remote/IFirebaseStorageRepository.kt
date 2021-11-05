package com.example.domain.repositories.remote

interface IFirebaseStorageRepository {
    suspend fun saveAvatar(avatar: Any): String
}
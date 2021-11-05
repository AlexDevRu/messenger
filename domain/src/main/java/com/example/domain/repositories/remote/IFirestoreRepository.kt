package com.example.domain.repositories.remote

interface IFirestoreRepository {
    suspend fun savePhoneByUserId(phoneNumber: String)
    suspend fun getPhoneByUserId(userId: String):String
    suspend fun getPhonesByUserIds(userIds: List<String>): Map<String, String>
}
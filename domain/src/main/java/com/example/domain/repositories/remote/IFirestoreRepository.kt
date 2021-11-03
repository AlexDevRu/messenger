package com.example.domain.repositories.remote

import com.example.domain.common.Result

interface IFirestoreRepository {
    suspend fun savePhoneByUserId(userId: String, phone: String)
    suspend fun getPhoneByUserId(userId: String):String
}
package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirestoreRepository

class SavePhoneUseCase(private val firestoreRepository: IFirestoreRepository) {
    suspend operator fun invoke(userId: String, phone: String) = try {
        firestoreRepository.savePhoneByUserId(userId, phone)
        Result.Success(Unit)
    } catch(e: Exception) {
        Result.Failure(e)
    }
}
package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IFirestoreRepository

class LinkPhoneToAccountUseCase(
    private val fireAuthRepository: IFirebaseAuthRepository,
    private val firestoreRepository: IFirestoreRepository
) {
    suspend operator fun invoke(phoneNumber: String, verificationId: String, smsCode: String) = try {
        fireAuthRepository.linkWithCredentials(verificationId, smsCode)
        firestoreRepository.savePhoneByUserId(phoneNumber)
        Result.Success(Unit)
    } catch(e: Exception) {
        Result.Failure(e)
    }

    suspend operator fun invoke(phoneNumber: String, credentials: Any) = try {
        fireAuthRepository.linkWithCredentials(credentials)
        firestoreRepository.savePhoneByUserId(phoneNumber)
        Result.Success(Unit)
    } catch(e: Exception) {
        Result.Failure(e)
    }
}
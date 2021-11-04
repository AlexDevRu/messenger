package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirebaseAuthRepository

class LinkPhoneToAccountUseCase(
    private val fireAuthRepository: IFirebaseAuthRepository
) {
    suspend operator fun invoke(verificationId: String, smsCode: String) = try {
        fireAuthRepository.linkWithCredentials(verificationId, smsCode)
        Result.Success(Unit)
    } catch(e: Exception) {
        Result.Failure(e)
    }

    suspend operator fun invoke(credentials: Any) = try {
        fireAuthRepository.linkWithCredentials(credentials)
        Result.Success(Unit)
    } catch(e: Exception) {
        Result.Failure(e)
    }
}
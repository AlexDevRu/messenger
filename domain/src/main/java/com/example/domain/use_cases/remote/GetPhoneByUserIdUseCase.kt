package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirestoreRepository

class GetPhoneByUserIdUseCase(
    private val firestoreRepository: IFirestoreRepository,
    private val usersRepository: IUsersRepository
) {
    suspend operator fun invoke(uid: String) = try {
        val phoneNumber = firestoreRepository.getPhoneByUserId(uid)
        usersRepository.updatePhoneByUserId(uid, phoneNumber)
        Result.Success(phoneNumber)
    } catch(e: Exception) {
        Result.Failure(e)
    }
}

package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class SignInUserUseCase(
    private val firestoreRepository: IFirestoreRepository,
    private val streamRepository: IStreamChatRepository,
    private val preferencesRepository: IPreferencesRepository
) {

    suspend operator fun invoke(userId: String, password: String, rememberMe: Boolean): Result<Unit> {
        return try {
            val result = firestoreRepository.checkUserCredentials(userId, password)
            when(result) {
                is Result.Success -> {
                    preferencesRepository.saveUser(if(rememberMe) userId else null)
                    streamRepository.connectUser(userId)
                }
                is Result.Failure -> throw result.throwable
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend operator fun invoke(userId: String) = streamRepository.connectUser(userId)
}
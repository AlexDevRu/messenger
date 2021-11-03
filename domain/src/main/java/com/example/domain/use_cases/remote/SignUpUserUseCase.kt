package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class SignUpUserUseCase(
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamRepository: IStreamChatRepository,
    private val preferencesRepository: IPreferencesRepository
) {

    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Result<Unit> {
        return try {
            val result = firebaseAuthRepository.createNewUser(email, password)
            when(result) {
                is Result.Success -> {
                    val userUID = result.value
                    preferencesRepository.saveUser(if(rememberMe) userUID else null)
                    streamRepository.connectUser(userUID, email)
                }
                is Result.Failure -> throw result.throwable
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class SignInUserUseCase(
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamRepository: IStreamChatRepository,
    private val preferencesRepository: IPreferencesRepository
) {

    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Result<ChatUser> {
        return try {
            val uid = firebaseAuthRepository.checkUserCredentials(email, password)
            preferencesRepository.saveUser(if(rememberMe) uid else null)
            val user = streamRepository.connectUser(uid)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend operator fun invoke(userId: String) = try {
        Result.Success(streamRepository.connectUser(userId))
    } catch(e: Exception) {
        Result.Failure(e)
    }
}
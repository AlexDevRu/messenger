package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class SignInUserUseCase(
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamRepository: IStreamChatRepository,
    private val usersRepository: IUsersRepository,
    private val preferencesRepository: IPreferencesRepository
) {

    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Result<ChatUser> {
        return try {
            println("SignInUserUseCase 1")
            val uid = firebaseAuthRepository.checkUserCredentials(email, password)
            println("SignInUserUseCase 2")
            val user = streamRepository.connectUser(uid)
            println("SignInUserUseCase 3")
            preferencesRepository.saveUser(if(rememberMe) uid else null)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend operator fun invoke(userId: String) = try {
        Result.Success(streamRepository.connectUser(userId))
    } catch(e: Exception) {
        try {
            val user = usersRepository.getUserById(userId)
            println("SIGNIN USER USE CASE $user")
            Result.Success(user)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}
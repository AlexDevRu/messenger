package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class SignUpUserUseCase(
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamRepository: IStreamChatRepository,
    private val preferencesRepository: IPreferencesRepository,
    private val usersRepository: IUsersRepository
) {

    suspend operator fun invoke(email: String, password: String, userName: String, rememberMe: Boolean): Result<ChatUser> {
        return try {
            val uid = firebaseAuthRepository.createNewUser(email, password)
            val user = streamRepository.connectUser(uid, userName, email)
            usersRepository.saveUser(user)
            preferencesRepository.saveUser(if(rememberMe) uid else null)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
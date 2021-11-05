package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class UpdateUserUseCase(
    private val fireStorageRepository: IFirebaseStorageRepository,
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamRepository: IStreamChatRepository,
    private val usersRepository: IUsersRepository
) {
    suspend operator fun invoke(name: String, photoData: Any): Result<ChatUser> {

        var imagePending = true
        var userPending = true

        return try {
            val photoUrl = fireStorageRepository.saveAvatar(photoData)
            imagePending = false
            val user = firebaseAuthRepository.updateCurrentUser(name, photoUrl)
            streamRepository.updateCurrentUser(name, photoUrl)
            userPending = false

            usersRepository.saveUser(user)

            Result.Success(user)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}
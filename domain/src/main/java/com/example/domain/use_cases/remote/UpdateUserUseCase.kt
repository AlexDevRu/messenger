package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class UpdateUserUseCase(
    private val fireStorageRepository: IFirebaseStorageRepository,
    private val streamRepository: IStreamChatRepository,
    private val usersRepository: IUsersRepository
) {
    suspend operator fun invoke(name: String, photoData: Any): Result<ChatUser> {
        return try {
            val photoUrl = fireStorageRepository.saveAvatar(photoData)
            val user = streamRepository.updateCurrentUser(name, photoUrl)
            usersRepository.saveUser(user)
            Result.Success(user)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}
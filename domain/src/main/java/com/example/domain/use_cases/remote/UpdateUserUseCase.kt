package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class UpdateUserUseCase(
    private val fireStorageRepository: IFirebaseStorageRepository,
    private val fireStoreRepository: IFirestoreRepository,
    private val streamChatRepository: IStreamChatRepository
) {
    suspend operator fun invoke(userId: String, name: String, phone: String, avatar: Any): Result<ChatUser> {
        return try {
            fireStoreRepository.savePhoneByUserId(userId, phone)

            val user = if(avatar is String) {
                streamChatRepository.partialUpdateUser(userId, name, avatar)
            } else {
                val avatarResult = fireStorageRepository.saveAvatar(userId, avatar)
                streamChatRepository.partialUpdateUser(userId, name, avatarResult!!)
            }

            Result.Success(user.copy(phone = phone))
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}
package com.example.domain.use_cases.remote

import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IStreamChatRepository
import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirestoreRepository

class GetUserByIdUseCase(
    private val streamChatRepository: IStreamChatRepository,
    private val firestoreRepository: IFirestoreRepository
) {
    suspend operator fun invoke(userId: String): Result<ChatUser> {
        return try {
            val user = streamChatRepository.getUserById(userId)
            val phone = firestoreRepository.getPhoneByUserId(userId)
            Result.Success(user.copy(phone = phone))
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}
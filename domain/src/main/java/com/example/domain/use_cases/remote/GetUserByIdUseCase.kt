package com.example.domain.use_cases.remote

import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IStreamChatRepository
import com.example.domain.common.Result
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirestoreRepository

class GetUserByIdUseCase(
    private val streamChatRepository: IStreamChatRepository,
    private val firestoreRepository: IFirestoreRepository,
    private val usersRepository: IUsersRepository
) {
    suspend operator fun invoke(userId: String): Result<ChatUser> {
        return try {
            val user = streamChatRepository.getUserById(userId)
            val phone = firestoreRepository.getPhoneByUserId(userId)
            usersRepository.saveUser(user.copy(phone = phone))
            Result.Success(user.copy(phone = phone))
        } catch(e: Exception) {
            if(e !is UserNotFoundException) {
                val user = usersRepository.getUserById(userId)
                Result.Success(user)
            }
            Result.Failure(e)
        }
    }
}
package com.example.domain.use_cases.remote

import com.example.domain.repositories.remote.IStreamChatRepository

class GetUserByIdUseCase(private val streamChatRepository: IStreamChatRepository) {
    suspend operator fun invoke(userId: String) = streamChatRepository.getUserById(userId)
}
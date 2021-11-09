package com.example.domain.use_cases.remote

import com.example.domain.repositories.remote.IStreamChatRepository

class GetUsersByQueryUseCase(
    private val streamChatRepository: IStreamChatRepository
) {
    operator fun invoke(query: String) = streamChatRepository.getUserPagerFlow(query)
}
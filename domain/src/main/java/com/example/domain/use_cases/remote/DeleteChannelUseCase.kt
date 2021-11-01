package com.example.domain.use_cases.remote

import com.example.domain.repositories.remote.IStreamChatRepository

class DeleteChannelUseCase(private val streamChatRepository: IStreamChatRepository) {
    operator fun invoke(cid: String) = streamChatRepository.deleteChannel(cid)
}
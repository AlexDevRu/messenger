package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IStreamChatRepository

class CreateChannelUseCase(
    private val streamChatRepository: IStreamChatRepository
) {
    suspend operator fun invoke(uid: String) = try {
        val cid = streamChatRepository.createChannel(uid)
        Result.Success(cid)
    } catch (e: Exception) {
        Result.Failure(e)
    }
}
package com.example.domain.repositories.remote

import com.example.domain.common.Result

interface IStreamChatRepository {
    suspend fun connectUser(userId: String): Result<Unit>
}
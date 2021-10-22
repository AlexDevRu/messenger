package com.example.data.repositories.remote

import com.example.domain.repositories.remote.IStreamChatRepository
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import java.lang.Exception
import com.example.domain.common.Result

class StreamChatRepository: IStreamChatRepository {

    private val client = ChatClient.instance()

    override suspend fun connectUser(userId: String): Result<Unit> {
        return try {
            val token = client.devToken(userId)

            val user = User(id = userId)

            val result = client.connectUser(user, token).execute()

            if(result.isSuccess) Result.Success(Unit) else Result.Failure(Exception(result.error().message))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
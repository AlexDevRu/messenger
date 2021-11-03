package com.example.data.repositories.remote

import android.util.Log
import com.example.data.mappers.toDataModel
import com.example.data.mappers.toDomainModel
import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IStreamChatRepository
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.offline.ChatDomain

class StreamChatRepository: IStreamChatRepository {

    companion object {
        private const val TAG = "StreamChatRepository"
    }

    private val client = ChatClient.instance()
    private val domain = ChatDomain.instance()

    override suspend fun getUserById(userId: String): Result<ChatUser?> {
        return try {
            client.connectAnonymousUser().execute()
            val request = QueryUsersRequest(
                filter = Filters.eq("id", userId),
                offset = 0,
                limit = 1
            )
            val result = client.queryUsers(request).execute()
            client.disconnect()
            if(result.isSuccess) Result.Success(result.data().firstOrNull()?.toDomainModel()) else Result.Failure(Exception(result.error().message))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun connectUser(userId: String, email: String): Result<Unit> {
        return try {
            val token = client.devToken(userId)

            val user = ChatUser(userId, email)

            val result = client.connectUser(user.toDataModel(), token).execute()

            if(result.isSuccess) Result.Success(Unit) else Result.Failure(Exception(result.error().message))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun connectUser(userId: String): Result<Unit> {
        return try {
            val token = client.devToken(userId)

            val userResult = getUserById(userId)

            when(userResult) {
                is Result.Success -> {
                    val result = client.connectUser(userResult.value!!.toDataModel(), token).execute()
                    if(result.isSuccess) Result.Success(Unit) else Result.Failure(Exception(result.error().message))
                }
                is Result.Failure -> throw userResult.throwable
            }
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }

    override fun deleteChannel(cid: String) {
        Log.w(TAG, "deleteChannel")

        domain.deleteChannel(cid).enqueue { result ->
            if(result.isSuccess) {
                Log.w(TAG, "channel deleted")
            } else {
                Log.w(TAG, "channel delete error ${result.error().message}")
            }
        }
    }

    override suspend fun partialUpdateUser(userId: String, name: String, avatar: String): ChatUser {
        val setFields = mutableMapOf(
            "name" to name,
            "image" to avatar,
            "email" to client.getCurrentUser()!!.extraData["email"].toString()
        )
        // сделать email айди пользователя

        val result = client.partialUpdateUser(userId, setFields).execute()

        if(result.isSuccess) {
            Log.d(TAG, "user is updated successfully ${result.data()}")
            return result.data().toDomainModel()
        } else {
            Log.d(TAG, "user update error ${result.error().message}")
            throw Exception(result.error().message)
        }
    }
}
package com.example.data.repositories.remote

import android.util.Log
import com.example.data.mappers.toDataModel
import com.example.data.mappers.toDomainModel
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IStreamChatRepository
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.offline.ChatDomain
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class StreamChatRepository: IStreamChatRepository {

    companion object {
        private const val TAG = "StreamChatRepository"
    }

    private val client = ChatClient.instance()
    private val domain = ChatDomain.instance()

    private suspend fun connectAnonymousUser() {

    }

    override suspend fun getUserById(userId: String): ChatUser {

        /*suspendCoroutine<Unit> { continuation ->
            client.connectAnonymousUser().enqueue {
                if(it.isSuccess) continuation.resume(Unit)
                else continuation.resumeWithException(Exception(it.error().message))
            }
        }
        client.connectAnonymousUser().execute()

        val request = QueryUsersRequest(
            filter = Filters.eq("id", userId),
            offset = 0,
            limit = 1
        )

        return suspendCoroutine { continuation ->
            client.queryUsers(request).enqueue {
                client.disconnect()

                if(it.isSuccess) {
                    val user = it.data().firstOrNull()
                    if(user != null) continuation.resume(user.toDomainModel())
                    else continuation.resumeWithException(UserNotFoundException())
                } else
                    continuation.resumeWithException(Exception(it.error().message))
            }
        }*/

        client.connectAnonymousUser().execute()
        val request = QueryUsersRequest(
            filter = Filters.eq("id", userId),
            offset = 0,
            limit = 1
        )
        val result = client.queryUsers(request).execute()
        client.disconnect()
        return if(result.isSuccess) {
            val user = result.data().firstOrNull()
            user?.toDomainModel() ?: throw UserNotFoundException()
        } else throw Exception(result.error().message)
    }

    override suspend fun connectUser(userId: String, userName: String, email: String): ChatUser {
        val token = client.devToken(userId)

        val user = ChatUser(userId, email = email, userName = userName)

        return suspendCoroutine { continuation ->
            client.connectUser(user.toDataModel(), token).enqueue { result ->
                if(result.isSuccess)
                    continuation.resume(result.data().user.toDomainModel())
                else
                    continuation.resumeWithException(Exception(result.error().message))
            }
        }
    }

    override suspend fun connectUser(userId: String): ChatUser {
        val token = client.devToken(userId)

        val user = getUserById(userId)

        return suspendCoroutine { continuation ->
            client.connectUser(user.toDataModel(), token).enqueue { result ->
                if(result.isSuccess)
                    continuation.resume(result.data().user.toDomainModel())
                else
                    continuation.resumeWithException(Exception(result.error().message))
            }
        }
    }

    override suspend fun updateCurrentUser(userName: String, photoUrl: String): ChatUser {
        val setFields = mutableMapOf(
            "name" to userName,
            "image" to photoUrl
        )

        return suspendCoroutine { continuation ->
            client.partialUpdateUser(client.getCurrentUser()!!.id, setFields).enqueue { result ->
                if(result.isSuccess) {
                    val currentUser = client.getCurrentUser()!!
                    currentUser.name = userName
                    currentUser.image = photoUrl
                    Log.d(TAG, "user is updated successfully ${result.data()}")
                    continuation.resume(result.data().toDomainModel())
                } else {
                    Log.d(TAG, "user update error ${result.error().message}")
                    continuation.resumeWithException(Exception(result.error().message))
                }
            }
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

    override fun logout() {
        client.disconnect()
    }

    override suspend fun getUsersByQuery(query: String): List<ChatUser> {

        val id = client.getCurrentUser()!!.id

        val request = if(query.isEmpty()) {
            QueryUsersRequest(
                filter = Filters.ne("id", id),
                offset = 0,
                limit = 100
            )
        } else {
            val filters = Filters.and(
                Filters.autocomplete("name", query),
                Filters.ne("id", id)
            )
            QueryUsersRequest(
                filter = filters,
                offset = 0,
                limit = 100
            )
        }

        return suspendCoroutine { continuation ->
            client.queryUsers(request).enqueue { result ->
                if(result.isSuccess) continuation.resume(result.data().map { it.toDomainModel() })
                else continuation.resumeWithException(Exception(result.error().message))
            }
        }
    }
}
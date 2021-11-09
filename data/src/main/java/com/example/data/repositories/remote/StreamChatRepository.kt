package com.example.data.repositories.remote

import android.util.Log
import androidx.paging.*
import com.example.data.database.MessengerDatabase
import com.example.data.mappers.toDataModel
import com.example.data.mappers.toDomainModel
import com.example.data.remote_mediators.UsersRemoteMediator
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class StreamChatRepository(
    private val database: MessengerDatabase,
    private val firestoreRepository: IFirestoreRepository
): IStreamChatRepository {

    companion object {
        private const val TAG = "StreamChatRepository"
    }

    private val client = ChatClient.instance()
    private val domain = ChatDomain.instance()

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

    override suspend fun createChannel(uid: String): String {
        return suspendCoroutine { continuation ->
            client.createChannel(
                channelType = "messaging",
                members = listOf(client.getCurrentUser()!!.id, uid)
            ).enqueue { result ->
                if (result.isSuccess) {
                    continuation.resume(result.data().cid)
                } else {
                    Log.e(TAG, result.error().message.toString())
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

    override suspend fun getUsersByIds(ids: List<String>): List<ChatUser> {
        if(ids.isEmpty()) return emptyList()

        val request = QueryUsersRequest(
            filter = Filters.`in`("id", ids),
            offset = 0,
            limit = 100
        )

        return suspendCoroutine { continuation ->
            client.queryUsers(request).enqueue { result ->
                if(result.isSuccess) continuation.resume(result.data().map { it.toDomainModel() })
                else continuation.resumeWithException(Exception(result.error().message))
            }
        }
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

    @ExperimentalPagingApi
    override fun getUserPagerFlow(query: String): Flow<PagingData<ChatUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 300,
                prefetchDistance = 1
            ),
            remoteMediator = UsersRemoteMediator(
                query,
                firestoreRepository,
                database
            ),
            pagingSourceFactory = {
                val dbQuery = "%${query.trim().lowercase().replace(' ', '%')}%"
                val excludeId = if(Firebase.auth.currentUser != null) Firebase.auth.currentUser!!.uid else ""
                database.userDao().getPaginatedUsersByQuery(dbQuery, excludeId)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                it.toDomainModel()
            }
        }
    }
}
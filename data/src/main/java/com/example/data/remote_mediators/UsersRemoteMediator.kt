package com.example.data.remote_mediators

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.data.database.MessengerDatabase
import com.example.data.database.entities.ChatUserWithMetadata
import com.example.data.database.entities.remote_keys.UserRemoteKeys
import com.example.data.mappers.toDomainModel
import com.example.data.mappers.toEntity
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IFirestoreRepository
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class UsersRemoteMediator(
    private val query: String,
    private val firestoreRepository: IFirestoreRepository,
    private val database: MessengerDatabase
): RemoteMediator<Int, ChatUserWithMetadata>() {

    companion object {
        private const val TAG = "UsersRemoteMediator"
    }

    private val client = ChatClient.instance()

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ChatUserWithMetadata>): UserRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { userWithMetadata ->
                // Get the remote keys of the last item retrieved
                database.userRemoteKeysDao().remoteKeysUserId(userWithMetadata.user.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ChatUserWithMetadata>): UserRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { userWithMetadata ->
                // Get the remote keys of the first items retrieved
                database.userRemoteKeysDao().remoteKeysUserId(userWithMetadata.user.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ChatUserWithMetadata>
    ): UserRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.user?.id?.let { userId ->
                database.userRemoteKeysDao().remoteKeysUserId(userId)
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatUserWithMetadata>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        Log.w(TAG, "page = $page, pageSize = ${state.config.pageSize}")

        try {

            val id = client.getCurrentUser()!!.id

            val request = if(query.isEmpty()) {
                QueryUsersRequest(
                    filter = Filters.ne("id", id),
                    offset = page * state.config.pageSize,
                    limit = state.config.pageSize
                )
            } else {
                val filters = Filters.and(
                    Filters.autocomplete("name", query),
                    Filters.ne("id", id)
                )
                QueryUsersRequest(
                    filter = filters,
                    offset = page * state.config.pageSize,
                    limit = state.config.pageSize
                )
            }

            val users = suspendCoroutine<List<ChatUser>> { continuation ->
                client.queryUsers(request).enqueue { result ->
                    if(result.isSuccess) continuation.resume(result.data().map { it.toDomainModel() })
                    else continuation.resumeWithException(Exception(result.error().message))
                }
            }

            val endOfPaginationReached = users.isEmpty()

            if(!endOfPaginationReached) {
                val phones = firestoreRepository.getPhonesByUserIds(users.map { it.id })
                users.forEach {
                    it.phone = phones[it.id]
                }
            }

            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.userRemoteKeysDao().clearRemoteKeys()
                    database.userDao().clearAll()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                Log.e(TAG, "nextKey $nextKey, prevKey $prevKey")
                val keys = users.map {
                    UserRemoteKeys(userId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.userRemoteKeysDao().insertAll(keys)

                val entityList = users.map { it.toEntity() }
                database.userDao().saveUsers(entityList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            Log.e(TAG, "error stream ${exception.message}")
            Log.e(TAG, "error stream ${exception.cause?.message}")
            return MediatorResult.Error(exception)
        }
    }
}

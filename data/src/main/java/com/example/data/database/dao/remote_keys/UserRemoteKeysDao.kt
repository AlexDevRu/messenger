package com.example.data.database.dao.remote_keys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.remote_keys.UserRemoteKeys

@Dao
interface UserRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<UserRemoteKeys>)

    @Query("SELECT * FROM user_remote_keys WHERE userId = :userId")
    suspend fun remoteKeysUserId(userId: String): UserRemoteKeys?

    @Query("DELETE FROM user_remote_keys")
    suspend fun clearRemoteKeys()
}

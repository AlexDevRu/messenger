package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.ChatUserEntity
import com.example.data.database.entities.ChatUserImageEntity
import com.example.data.database.entities.ChatUserPhoneEntity
import com.example.data.database.entities.ChatUserWithMetadata

@Dao
interface UserDao {
    @Query("""
        SELECT * from users where
        userName like :query
        """)
    suspend fun getUsersByQuery(query: String): List<ChatUserWithMetadata>

    suspend fun saveUsers(users: List<ChatUserWithMetadata>) {
        for(user in users) saveUser(user)
    }

    suspend fun saveUser(user: ChatUserWithMetadata) {
        _saveUser(user.user)
        _saveImage(user.image)
        _savePhone(user.phone)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _saveUser(user: ChatUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _saveImage(user: ChatUserImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _savePhone(user: ChatUserPhoneEntity)

    @Query("select * from users where id=:id")
    suspend fun getUserById(id: String): ChatUserWithMetadata?
}
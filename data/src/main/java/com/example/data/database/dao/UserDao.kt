package com.example.data.database.dao

import androidx.paging.PagingSource
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

    @Query("select * from users where userName like :query and users.id != :excludeId")
    fun getPaginatedUsersByQuery(query: String, excludeId: String = ""): PagingSource<Int, ChatUserWithMetadata>

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

    @Query("update users_phones set phone=:phoneNumber where userId=:uid")
    suspend fun updatePhoneByUserId(uid: String, phoneNumber: String)

    @Query("select * from users where id=:id")
    suspend fun getUserById(id: String): ChatUserWithMetadata?

    @Query("delete from users")
    suspend fun clearAll()
}
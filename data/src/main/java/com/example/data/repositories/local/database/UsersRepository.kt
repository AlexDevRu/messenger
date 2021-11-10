package com.example.data.repositories.local.database

import com.example.data.database.dao.UserDao
import com.example.data.mappers.toDomainModel
import com.example.data.mappers.toEntity
import com.example.domain.models.ChatUser
import com.example.domain.repositories.local.database.IUsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UsersRepository(
    private val usersDao: UserDao
): IUsersRepository {

    override suspend fun getUserById(id: String): ChatUser? {
        return usersDao.getUserById(id)?.toDomainModel()
    }

    override suspend fun saveUsers(users: List<ChatUser>) {
        usersDao.saveUsers(users.map { it.toEntity() })
    }

    override suspend fun saveUser(user: ChatUser) {
        usersDao.saveUser(user.toEntity())
    }

    override suspend fun updatePhoneByUserId(uid: String, phoneNumber: String)
        = usersDao.updatePhoneByUserId(uid, phoneNumber)
}
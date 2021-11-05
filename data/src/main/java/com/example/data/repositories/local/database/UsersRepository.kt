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

    override suspend fun getUsersByQuery(query: String, excludeCurrentUser: Boolean): List<ChatUser> {
        val dbQuery = "%${query.trim().lowercase().replace(' ', '%')}%"
        val users = usersDao.getUsersByQuery(dbQuery).map { it.toDomainModel() }
        return if(excludeCurrentUser) users.filter { it.id != Firebase.auth.currentUser?.uid } else users
    }

    override suspend fun getUserById(id: String): ChatUser? {
        return usersDao.getUserById(id)?.toDomainModel()
    }

    override suspend fun saveUsers(users: List<ChatUser>) {
        usersDao.saveUsers(users.map { it.toEntity() })
    }

    override suspend fun saveUser(user: ChatUser) {
        usersDao.saveUser(user.toEntity())
    }
}
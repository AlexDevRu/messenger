package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class GetUsersByQueryUseCase(
    private val streamChatRepository: IStreamChatRepository,
    private val firestoreRepository: IFirestoreRepository,
    private val usersRepository: IUsersRepository
) {

    suspend operator fun invoke(query: String) = try {
        val users = streamChatRepository.getUsersByQuery(query)
        val phones = firestoreRepository.getPhonesByUserIds(users.map { it.id })
        users.forEach {
            it.phone = phones[it.id]
        }
        usersRepository.saveUsers(users)
        Result.Success(users)
    } catch(e: Exception) {
        println("GET USERS BY QUERY CATCH")
        try {
            val users = usersRepository.getUsersByQuery(query)
            println("USERS USE CASE $users")
            Result.Success(users)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}

package com.example.domain.use_cases.remote

import com.example.domain.common.Result
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class GetUsersByPhoneNumbersUseCase(
    private val firestoreRepository: IFirestoreRepository,
    private val streamRepository: IStreamChatRepository,
    private val usersRepository: IUsersRepository
) {
    suspend operator fun invoke(phones: List<String>) = try {
        val phonesMap = firestoreRepository.getUsersByPhones(phones)
        val users = streamRepository.getUsersByIds(phonesMap.keys.toList())
        for(user in users) {
            user.phone = phonesMap[user.id]
        }
        usersRepository.saveUsers(users)
        Result.Success(users)
    } catch(e: Exception) {
        Result.Failure(e)
    }
}

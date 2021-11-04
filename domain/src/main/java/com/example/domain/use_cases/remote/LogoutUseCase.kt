package com.example.domain.use_cases.remote

import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IStreamChatRepository

class LogoutUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val firebaseAuthRepository: IFirebaseAuthRepository,
    private val streamChatRepository: IStreamChatRepository
) {
    operator fun invoke() {
        preferencesRepository.saveUser(null)
        firebaseAuthRepository.logout()
        streamChatRepository.logout()
    }
}
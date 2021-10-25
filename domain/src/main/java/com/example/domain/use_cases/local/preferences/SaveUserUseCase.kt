package com.example.domain.use_cases.local.preferences

import com.example.domain.repositories.local.IPreferencesRepository

class SaveUserUseCase(private val preferencesRepository: IPreferencesRepository) {
    operator fun invoke(userId: String?) = preferencesRepository.saveUser(userId)
}
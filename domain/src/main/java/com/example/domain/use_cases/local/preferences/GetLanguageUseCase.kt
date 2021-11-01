package com.example.domain.use_cases.local.preferences

import com.example.domain.repositories.local.IPreferencesRepository

class GetLanguageUseCase(private val preferencesRepository: IPreferencesRepository) {
    operator fun invoke() = preferencesRepository.getLanguage()
}
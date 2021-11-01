package com.example.domain.use_cases.local.preferences

import com.example.domain.repositories.local.IPreferencesRepository

class SaveLanguageUseCase(private val preferencesRepository: IPreferencesRepository) {
    operator fun invoke(lang: String) = preferencesRepository.saveLanguage(lang)
}
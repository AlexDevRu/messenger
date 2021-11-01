package com.example.domain.use_cases.local.preferences

import com.example.domain.repositories.local.IPreferencesRepository

class GetThemeUseCase(private val preferencesRepository: IPreferencesRepository) {
    operator fun invoke() = preferencesRepository.getTheme()
}
package com.example.domain.use_cases.local.preferences

import com.example.domain.repositories.local.IPreferencesRepository

class SaveThemeUseCase(private val preferencesRepository: IPreferencesRepository) {
    operator fun invoke(isDark: Boolean) = preferencesRepository.saveTheme(isDark)
}
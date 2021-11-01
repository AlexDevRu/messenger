package com.example.chat.ui.settings

import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.base.UiEffect
import com.example.domain.use_cases.local.preferences.GetLanguageUseCase
import com.example.domain.use_cases.local.preferences.GetThemeUseCase
import com.example.domain.use_cases.local.preferences.SaveLanguageUseCase
import com.example.domain.use_cases.local.preferences.SaveThemeUseCase

class SettingsVM(
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase
): BaseViewModel<SettingsContract.Event, SettingsContract.State, UiEffect>() {

    init {
        setState { copy(isDark = getThemeUseCase(), lang = getLanguageUseCase()) }
    }

    override fun createInitialState(): SettingsContract.State {
        return SettingsContract.State(
            isDark = false,
            lang = ""
        )
    }

    override fun handleEvent(event: SettingsContract.Event) {
        when(event) {
            is SettingsContract.Event.OnThemeChanged -> {
                saveThemeUseCase(event.isDark)
                setState { copy(isDark = event.isDark) }
            }
            is SettingsContract.Event.OnLanguageChanged -> {
                saveLanguageUseCase(event.lang)
                setState { copy(lang = event.lang) }
            }
        }
    }
}
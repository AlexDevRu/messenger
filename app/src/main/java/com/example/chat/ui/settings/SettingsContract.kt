package com.example.chat.ui.settings

import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState

object SettingsContract {
    sealed class Event : UiEvent {
        data class OnThemeChanged(val isDark: Boolean): Event()
        data class OnLanguageChanged(val lang: String): Event()
    }

    data class State(
        val isDark: Boolean,
        val lang: String
    ): UiState
}
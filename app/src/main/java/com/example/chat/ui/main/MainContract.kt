package com.example.chat.ui.main

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import io.getstream.chat.android.client.models.User

object MainContract {
    sealed class Event : UiEvent {
        data class OnUserLoad(val userId: String?): Event()
        object OnLogout: Event()
    }

    data class State(
        val user: User? = null,
        val loading: Boolean = false
    ): UiState

    sealed class Effect: UiEffect {
        object Logout: Effect()
        data class ShowErrorSnackbar(val message: String?): Effect()
    }
}
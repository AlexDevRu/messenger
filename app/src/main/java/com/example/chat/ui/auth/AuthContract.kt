package com.example.chat.ui.auth

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.domain.models.ChatUser

object AuthContract {
    sealed class Event : UiEvent {
        object OnSignInClicked: Event()
        data class OnSignInStatusChanged(val status: Boolean): Event()
        object OnModeChanged: Event()
    }

    data class State(
        val rememberMe: Boolean = true,
        val loading: Boolean = false,
        val mode: SIGN_MODE = SIGN_MODE.SIGN_IN
    ): UiState

    sealed class Effect : UiEffect {
        data class SignInSuccess(val user: ChatUser): Effect()
        data class SignUpSuccess(val user: ChatUser): Effect()
        data class AuthFailure(val throwable: Throwable?): Effect()
    }

    enum class SIGN_MODE {
        SIGN_IN {
            override fun toggle() = SIGN_UP
        },
        SIGN_UP {
            override fun toggle() = SIGN_IN
        };

        abstract fun toggle(): SIGN_MODE
    }
}
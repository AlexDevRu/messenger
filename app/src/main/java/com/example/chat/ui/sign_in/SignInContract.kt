package com.example.chat.ui.sign_in

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.chat.ui.validation.InputValidationError

object SignInContract {
    sealed class Event : UiEvent {
        object OnSignInClicked: Event()
        data class OnValidateUserName(val userName: String): Event()
        data class OnValidatePassword(val password: String): Event()
        data class OnSignInStatusChanged(val status: Boolean): Event()
        object OnModeChanged: Event()
    }

    data class State(
        val userName: String? = null,
        val userNameValidationError: List<InputValidationError>? = null,
        val password: String? = null,
        val passwordValidationError: List<InputValidationError>? = null,
        val saveSignInStatus: Boolean = true,
        val loading: Boolean = false,
        val mode: SIGN_MODE = SIGN_MODE.SIGN_IN
    ): UiState

    sealed class Effect : UiEffect {
        object SignInSuccess: Effect()
        data class SignInFailure(val throwable: Throwable?): Effect()
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
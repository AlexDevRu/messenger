package com.example.chat.ui.sign_in

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState

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

    sealed class InputValidationError {
        data class LessCharactersError(val minCharacters: Int): InputValidationError() {
            override fun validate(input: String) = input.isNotEmpty() && input.length >= minCharacters
        }

        object NoNumbersError : InputValidationError() {
            override fun validate(input: String) = Regex("\\d").containsMatchIn(input)
        }

        object SameCaseError : InputValidationError() {
            override fun validate(input: String) = Regex("[a-z][A-Z]|[A-Z][a-z]").containsMatchIn(input)
        }

        abstract fun validate(input: String): Boolean
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
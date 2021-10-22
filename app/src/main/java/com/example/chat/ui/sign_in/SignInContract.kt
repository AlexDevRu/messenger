package com.example.chat.ui.sign_in

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState

object SignInContract {
    sealed class Event : UiEvent {
        object OnSignInClicked: Event()
        data class OnValidateUserName(val userName: String): Event()
        data class OnValidateFirstName(val firstName: String): Event()
    }

    data class State(
        val userName: String? = null,
        val userNameValidationError: InputValidationError? = null,
        val firstName: String? = null,
        val firstNameValidationError: InputValidationError? = null,
        val loading: Boolean = false
    ): UiState

    sealed class Effect : UiEffect {
        object SignInSuccess: Effect()
        data class SignInFailure(val message: String?): Effect()
    }

    sealed class InputValidationError {
        data class LessCharactersException(val minCharacters: Int): InputValidationError()
    }
}
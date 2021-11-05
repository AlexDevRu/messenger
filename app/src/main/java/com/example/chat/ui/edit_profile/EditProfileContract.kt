package com.example.chat.ui.edit_profile

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.chat.ui.validation.InputValidator
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User

object EditProfileContract {
    sealed class Event: UiEvent {
        object OnApplyChanges: Event()
        data class OnFirstNameChanged(val firstName: String): Event()
        data class OnImageUpload(val data: Any): Event()
    }

    data class State(
        val userName: String,
        val userNameValidationError: List<InputValidator>?,
        val applyChangedInProgress: Boolean,
        val avatar: Any,
    ): UiState

    sealed class Effect: UiEffect {
        data class UserUpdatedSuccessfully(val user: ChatUser): Effect()
        data class UserUpdateFailure(val message: String?): Effect()
        data class SetUserData(val user: User): Effect()
    }
}
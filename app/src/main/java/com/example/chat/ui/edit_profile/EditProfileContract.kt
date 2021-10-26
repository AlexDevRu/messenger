package com.example.chat.ui.edit_profile

import android.graphics.Bitmap
import android.net.Uri
import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.chat.ui.validation.InputValidationError
import io.getstream.chat.android.client.models.User

object EditProfileContract {
    sealed class Event: UiEvent {
        object OnApplyChanges: Event()
        data class OnFirstNameChanged(val firstName: String): Event()
        data class OnImageUpload(val data: Any): Event()
    }

    data class State(
        val userName: String,
        val firstName: String,
        val firstNameValidationError: List<InputValidationError>? = null,
        val applyChangedInProgress: Boolean = false,
        val avatar: Any
    ): UiState

    sealed class Effect: UiEffect {
        data class UserUpdatedSuccessfully(val user: User?): Effect()
        data class UserUpdateFailure(val message: String?): Effect()
    }
}
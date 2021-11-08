package com.example.chat.ui.auth

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.domain.models.ChatUser

object NewAvatarSignUpContract {
    sealed class Event: UiEvent {
        object OnConfirm: Event()
        data class SetUserData(val user: ChatUser): Event()
    }

    data class State(
        val loading: Boolean,
    ): UiState

    sealed class Effect: UiEffect {
        data class UploadSuccessfully(val user: ChatUser): Effect()
        data class UploadFailure(val message: String): Effect()
    }
}
package com.example.chat.user_info

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.domain.models.ChatUser

object UserInfoContract {

    sealed class Event: UiEvent {
        data class OnGetUserPhoneById(val uid: String): Event()
        data class OnAddContact(val userName: String): Event()
    }

    data class State(
        val phone: String?,
        val loading: Boolean,
        val isContactInsertInProgress: Boolean
    ): UiState

    sealed class Effect: UiEffect {
        data class ShowErrorMessage(val message: String): Effect()
    }
}
package com.example.chat.ui.users

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import io.getstream.chat.android.client.models.User

object UsersContract {
    sealed class Event : UiEvent {
        data class OnQueryChanged(val query: String): Event()
        data class OnUserClick(val userId: String): Event()
    }

    data class State(
        val users: List<User>? = null,
        val query: String = "",
        val loading: Boolean = false
    ): UiState

    sealed class Effect : UiEffect {
        data class SearchFailure(val message: String?): Effect()
        data class GoToChat(val cid: String): Effect()
    }
}
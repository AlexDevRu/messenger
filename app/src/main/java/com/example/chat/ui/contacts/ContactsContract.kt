package com.example.chat.ui.contacts

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User

data class Contact(
    val contactName: String,
    val phoneNumbers: List<String>,
    var user: ChatUser? = null
)

object ContactsContract {
    sealed class Event: UiEvent {
        object OnReadContacts: Event()
        data class OnUserClick(val userId: String): Event()
    }

    data class State(
        val users: List<User>,
        val contacts: List<Contact>,
        val loading: Boolean = false
    ): UiState

    sealed class Effect: UiEffect {
        data class GoToChat(val cid: String): Effect()
        data class ShowErrorMessage(val message: String): Effect()
    }
}
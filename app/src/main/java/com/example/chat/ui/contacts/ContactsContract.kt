package com.example.chat.ui.contacts

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState

data class Contact(
    val name: String,
    val phoneNumber: String
)

object ContactsContract {
    sealed class Event: UiEvent {
        object OnReadContacts: Event()
    }

    data class State(
        val contacts: List<Contact>,
        val loading: Boolean = false
    ): UiState

    sealed class Effect: UiEffect {

    }
}
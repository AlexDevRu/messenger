package com.example.chat.ui.contacts

import androidx.compose.runtime.Composable
import com.example.chat.R
import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User

typealias ComposableFun = @Composable () -> Unit
sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    object Contacts : TabItem(R.drawable.ic_baseline_phone_24, "All contacts", { ContactsTabScreen() })
    object Users : TabItem(R.drawable.ic_baseline_people_alt_24, "Users", { UsersTabScreen() })
}

data class Contact(
    val contactName: String,
    val phoneNumbers: List<String>,
    var user: ChatUser? = null
)

object ContactsContract {
    sealed class Event: UiEvent {
        object OnReadContacts: Event()
    }

    data class State(
        val users: List<User>,
        val contacts: List<Contact>,
        val loading: Boolean = false
    ): UiState

    sealed class Effect: UiEffect {

    }
}
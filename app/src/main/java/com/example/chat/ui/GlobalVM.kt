package com.example.chat.ui

import androidx.lifecycle.ViewModel
import com.example.data.mappers.toDomainModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GlobalVM: ViewModel() {

    private val _user = MutableStateFlow<ChatUser?>(null)
    val user: StateFlow<ChatUser?> = _user

    fun reloadCurrentUser() {
        val user = ChatClient.instance().getCurrentUser()?.toDomainModel()
        _user.value = user
    }
}
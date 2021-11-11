package com.example.chat.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.use_cases.remote.DeleteChannelUseCase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatVM(
    private val deleteChannelUseCase: DeleteChannelUseCase
): ViewModel() {

    companion object {
        private const val TAG = "ChatVM"
    }

    fun deleteChannel(cid: String) = deleteChannelUseCase(cid)

    init {
        viewModelScope.launch {
            ChatDomain.instance().typingUpdates.collect {
                Log.w(TAG, "typing")
            }
            ChatClient.instance()
        }
    }
}
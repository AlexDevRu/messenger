package com.example.chat.ui.chat

import androidx.lifecycle.ViewModel
import com.example.domain.use_cases.remote.DeleteChannelUseCase

class ChatVM(
    private val deleteChannelUseCase: DeleteChannelUseCase
): ViewModel() {

    companion object {
        private const val TAG = "ChatVM"
    }

    fun deleteChannel(cid: String) = deleteChannelUseCase(cid)
}
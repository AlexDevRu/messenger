package com.example.chat.ui.channels

import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import io.getstream.chat.android.compose.state.channel.list.ChannelsState

object ChannelsContract {
    sealed class Event : UiEvent {
        data class Init(val userId: String): Event()
    }

    data class State(
        val channelsState: ChannelsState = ChannelsState()
    ): UiState

    sealed class Effect: UiEffect {
        data class ShowErrorSnackbar(val message: String?): Effect()
    }
}
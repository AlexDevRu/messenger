package com.example.chat.ui.main

import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.compose.state.channel.list.ChannelsState
import io.getstream.chat.android.offline.ChatDomain
import io.getstream.chat.android.offline.querychannels.QueryChannelsController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChannelsVM()
    : BaseViewModel<ChannelsContract.Event, ChannelsContract.State, ChannelsContract.Effect>() {

    private val domain = ChatDomain.instance()

    override fun createInitialState(): ChannelsContract.State {
        return ChannelsContract.State(
            channelsState = ChannelsState()
        )
    }

    override fun handleEvent(event: ChannelsContract.Event) {
        when(event) {
            is ChannelsContract.Event.Init -> queryAllChannels(event.userId)
        }
    }

    private val channels: StateFlow<List<Channel>>? = null

    private var channelsJob: Call<QueryChannelsController>? = null

    private fun queryAllChannels(userId: String) {
        val filter = Filters.and(
            Filters.`in`("members", listOf(userId)),
        )

        channelsJob?.cancel()
        channelsJob = domain.queryChannels(filter, QuerySort.desc("last_updated"))

        channelsJob?.enqueue { result ->
            if (result.isSuccess) {
                val queryChannelsController = result.data()

                viewModelScope.launch {
                    queryChannelsController.loading.collect { loading ->
                        setState { copy(channelsState.copy(isLoading = loading)) }
                    }
                }
                viewModelScope.launch {
                    queryChannelsController.channels.collectLatest { channels ->
                        setState { copy(channelsState.copy(channels = channels)) }
                    }
                }
                viewModelScope.launch {
                    queryChannelsController.loadingMore.collectLatest { loadingMore ->
                        setState { copy(channelsState.copy(isLoadingMore = loadingMore)) }
                    }
                }
                viewModelScope.launch {
                    queryChannelsController.endOfChannels.collect { endOfChannels ->
                        setState { copy(channelsState.copy(endOfChannels = endOfChannels)) }
                    }
                }
            }
        }
    }
}
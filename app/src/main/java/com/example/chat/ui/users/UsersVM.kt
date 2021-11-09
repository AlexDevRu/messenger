package com.example.chat.ui.users

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.CreateChannelUseCase
import com.example.domain.use_cases.remote.GetUsersByQueryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class UsersVM(
    private val createChannelUseCase: CreateChannelUseCase,
    private val getUsersByQueryUseCase: GetUsersByQueryUseCase
): BaseViewModel<UsersContract.Event, UsersContract.State, UsersContract.Effect>() {

    companion object {
        private const val TAG = "UsersVM"
        private const val SEARCH_DELAY = 1500L
    }

    private var searchJob: Job? = null

    init {
        getUsersByQuery(false)
    }

    override fun createInitialState(): UsersContract.State {
        return UsersContract.State(
            users = null,
            query = "",
            loading = false,
            usersFlow = emptyFlow()
        )
    }

    override fun handleEvent(event: UsersContract.Event) {
        when(event) {
            is UsersContract.Event.OnQueryChanged -> {
                setState { copy(query = event.query) }
                getUsersByQuery()
            }
            is UsersContract.Event.OnUserClick -> createNewChannel(event.userId)
        }
    }

    private fun createNewChannel(selectedUserId: String) {
        viewModelScope.launch {
            val result = createChannelUseCase(selectedUserId)
            when(result) {
                is Result.Success -> setEffect { UsersContract.Effect.GoToChat(result.value) }
                is Result.Failure -> {}
            }
        }
    }

    private fun getUsersByQuery(withDelay: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if(withDelay) delay(SEARCH_DELAY)
            val usersFlow = getUsersByQueryUseCase(currentState.query).cachedIn(viewModelScope)
            setState { copy(usersFlow = usersFlow) }
        }
    }
}
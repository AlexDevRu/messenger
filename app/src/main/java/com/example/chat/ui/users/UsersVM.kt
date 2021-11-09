package com.example.chat.ui.users

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.data.mappers.toDataModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.CreateChannelUseCase
import com.example.domain.use_cases.remote.GetUsersByQueryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersVM(
    private val getUsersByQueryUseCase: GetUsersByQueryUseCase,
    private val createChannelUseCase: CreateChannelUseCase
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
            loading = false
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

    private fun getUsersByQuery(withDelay: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if(withDelay) delay(SEARCH_DELAY)
            setState { copy(loading = true) }
            val result = getUsersByQueryUseCase(currentState.query)
            when(result) {
                is Result.Success -> {
                    Log.d(TAG, "users ${result.value}")
                    setState { copy(loading = false, users = result.value.map { it.toDataModel() }) }
                }
                is Result.Failure -> {
                    setEffect { UsersContract.Effect.SearchFailure(result.throwable.message) }
                    setState { copy(loading = false) }
                }
            }
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
}
package com.example.chat.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import com.example.domain.use_cases.local.preferences.SaveUserUseCase
import com.example.domain.use_cases.remote.SignInUserUseCase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainVM(
    private val signInUserUseCase: SignInUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserUseCase: GetUserUseCase
): BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    private val client = ChatClient.instance()

    init {
        setEvent(MainContract.Event.OnUserLoad(null))
    }

    private fun getUser() {
        if(client.getCurrentUser() == null) {
            val userId = getUserUseCase()
            if(userId != null) fetchUser(userId)
        } else {
            setState { copy(user = client.getCurrentUser()) }
        }
    }

    private fun fetchUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loading = true) }
            val result = signInUserUseCase(userId)
            when(result) {
                is Result.Success -> setState { copy(user = ChatClient.instance().getCurrentUser()) }
                is Result.Failure -> setEffect { MainContract.Effect.ShowErrorSnackbar(result.throwable.message) }
            }
            setState { copy(loading = false) }
        }
    }

    private fun logout() {
        client.disconnect()
        saveUserUseCase(null)
        setEffect { MainContract.Effect.Logout }
    }

    override fun createInitialState(): MainContract.State {
        Log.d("asd", "CREATE INITIAL STATE")
        return MainContract.State(
            user = null,
            loading = false
        )
    }

    override fun handleEvent(event: MainContract.Event) {
        when(event) {
            is MainContract.Event.OnUserLoad -> getUser()
            is MainContract.Event.OnUserUpdated -> setState { copy(user = event.user) }
            MainContract.Event.OnLogout -> logout()
        }
    }

    private val TAG = "MainVM"

    fun deleteChannel(channelId: String) {
        Log.w(TAG, "deleteChannel")

        ChatDomain.instance().deleteChannel(channelId).enqueue { result ->
            if(result.isSuccess) {
                Log.w(TAG, "channel deleted")
            } else {
                Log.w(TAG, "channel delete error ${result.error().message}")
                setEffect { MainContract.Effect.ShowErrorSnackbar(result.error().message) }
            }
        }
    }
}
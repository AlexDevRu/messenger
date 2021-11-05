package com.example.chat.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.data.mappers.toDataModel
import com.example.data.mappers.toDomainModel
import com.example.data.models.getAvatarOrDefault
import com.example.domain.common.Result
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import com.example.domain.use_cases.remote.DeleteChannelUseCase
import com.example.domain.use_cases.remote.LogoutUseCase
import com.example.domain.use_cases.remote.SignInUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainVM(
    private val signInUserUseCase: SignInUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val deleteChannelUseCase: DeleteChannelUseCase,
    private val logoutUseCase: LogoutUseCase
): BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    companion object {
        private const val TAG = "MainVM"
    }

    init {
        setEvent(MainContract.Event.OnUserLoad(null))
    }

    private fun getUser() {

        val user = ChatClient.instance().getCurrentUser()
        Log.d("asd", "current user ${user}")

        if(user == null) {
            val userId = getUserUseCase()
            Log.d("asd", "current user userId ${userId}")
            if(userId != null) {
                fetchUser(userId)
            }
        }

    }

    private fun fetchUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loading = true) }
            val result = signInUserUseCase(userId)
            when(result) {
                is Result.Success -> {
                    Log.e("asd", "current user fetched ${result.value}")
                    val user = result.value
                    user?.email = Firebase.auth.currentUser?.email!!
                    user?.userName = Firebase.auth.currentUser?.displayName!!
                    user?.avatar = Firebase.auth.currentUser!!.toDomainModel().getAvatarOrDefault()
                    setState { copy(user = user) }
                }
                is Result.Failure -> setEffect { MainContract.Effect.ShowErrorSnackbar(result.throwable.message) }
            }
            setState { copy(loading = false) }
        }
    }

    private fun logout() {
        logoutUseCase()
        setEffect { MainContract.Effect.Logout }
    }

    override fun createInitialState(): MainContract.State {
        return MainContract.State(
            user = Firebase.auth.currentUser?.toDomainModel(),
            loading = false
        )
    }

    override fun handleEvent(event: MainContract.Event) {
        when(event) {
            is MainContract.Event.OnUserLoad -> getUser()
            is MainContract.Event.OnUserUpdated -> setState { copy(user = Firebase.auth.currentUser?.toDomainModel()) }
            MainContract.Event.OnLogout -> logout()
        }
    }

    fun deleteChannel(cid: String) = deleteChannelUseCase(cid)
}
package com.example.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mappers.toDomainModel
import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import com.example.domain.use_cases.remote.GetUserByIdUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GlobalVM(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserUseCase: GetUserUseCase
): ViewModel() {

    private val _user = MutableStateFlow<ChatUser?>(null)
    val user: StateFlow<ChatUser?> = _user

    private var job: Job? = null

    init {
        //reloadCurrentUser(getUserUseCase())
    }

    fun reloadCurrentUser(uid: String? = Firebase.auth.currentUser?.uid) {
        /*job?.cancel()
        if(uid != null) {
            job = viewModelScope.launch(Dispatchers.IO) {
                val result = getUserByIdUseCase(uid)
                when(result) {
                    is Result.Success -> {
                        _user.value = result.value
                    }
                }
            }
        }*/
        val user = ChatClient.instance().getCurrentUser()?.toDomainModel()
        _user.value = user
    }

    /*fun setUser(user: ChatUser?) {
        _user.value = user
    }*/
}
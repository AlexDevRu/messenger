package com.example.chat.ui.auth

import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.chat.ui.base.composables.CustomImageVM
import com.example.domain.common.Result
import com.example.domain.models.ChatUser
import com.example.domain.use_cases.remote.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NewAccountSettingsContract {
    sealed class Event: UiEvent {
        object OnConfirm: Event()
        data class SetUserData(val user: ChatUser): Event()
    }

    data class State(
        val loading: Boolean,
    ): UiState

    sealed class Effect: UiEffect {
        data class UploadSuccessfully(val user: ChatUser): Effect()
        data class UploadFailure(val message: String): Effect()
    }
}

class NewAccountSettingsVM(
    private val updateUserUseCase: UpdateUserUseCase
): BaseViewModel<NewAccountSettingsContract.Event, NewAccountSettingsContract.State, NewAccountSettingsContract.Effect>() {

    val customImageVM = CustomImageVM(null)

    override fun createInitialState(): NewAccountSettingsContract.State {
        return NewAccountSettingsContract.State(
            loading = false,
        )
    }

    override fun handleEvent(event: NewAccountSettingsContract.Event) {
        when(event) {
            NewAccountSettingsContract.Event.OnConfirm -> uploadPhotoData()
            is NewAccountSettingsContract.Event.SetUserData -> {
                customImageVM.setUser(event.user)
            }
        }
    }

    private fun uploadPhotoData() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loading = true) }

            val result = updateUserUseCase(
                customImageVM.user.value?.userName.orEmpty(),
                customImageVM.user.value?.avatar ?: ""
            )
            when(result) {
                is Result.Success -> setEffect { NewAccountSettingsContract.Effect.UploadSuccessfully(result.value) }
                is Result.Failure -> setEffect { NewAccountSettingsContract.Effect.UploadFailure(result.throwable.message.orEmpty()) }
            }

            setState { copy(loading = false) }
        }
    }
}
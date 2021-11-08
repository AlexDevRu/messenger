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

class NewAvatarSignUpVM(
    private val updateUserUseCase: UpdateUserUseCase
): BaseViewModel<NewAvatarSignUpContract.Event, NewAvatarSignUpContract.State, NewAvatarSignUpContract.Effect>() {

    val customImageVM = CustomImageVM(null)

    override fun createInitialState(): NewAvatarSignUpContract.State {
        return NewAvatarSignUpContract.State(
            loading = false,
        )
    }

    override fun handleEvent(event: NewAvatarSignUpContract.Event) {
        when(event) {
            NewAvatarSignUpContract.Event.OnConfirm -> uploadPhotoData()
            is NewAvatarSignUpContract.Event.SetUserData -> customImageVM.setUser(event.user)
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
                is Result.Success -> setEffect { NewAvatarSignUpContract.Effect.UploadSuccessfully(result.value) }
                is Result.Failure -> setEffect { NewAvatarSignUpContract.Effect.UploadFailure(result.throwable.message.orEmpty()) }
            }

            setState { copy(loading = false) }
        }
    }
}
package com.example.chat.ui.edit_profile

import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.base.composables.CustomImageVM
import com.example.chat.ui.base.composables.TextFieldVM
import com.example.chat.ui.validation.InputValidator
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditProfileVM(
    private val updateUserUseCase: UpdateUserUseCase
): BaseViewModel<EditProfileContract.Event, EditProfileContract.State, EditProfileContract.Effect>() {

    companion object {
        private const val TAG = "EditProfileVM"
        private const val minCharacters = 4
        private const val maxCharacters = 20
    }

    private val userNameValidators = listOf(
        InputValidator.LessCharactersValidator(minCharacters)
    )

    val customImageVM = CustomImageVM(null)
    val userNameInputState = TextFieldVM(userNameValidators, maxCharacters)

    override fun createInitialState(): EditProfileContract.State {
        return EditProfileContract.State(
            applyChangedInProgress = false,
        )
    }

    override fun handleEvent(event: EditProfileContract.Event) {
        when(event) {
            EditProfileContract.Event.OnApplyChanges -> applyChanges()
        }
    }

    private fun applyChanges() {
        setState { copy(applyChangedInProgress = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = updateUserUseCase(userNameInputState.value, customImageVM.user.value?.avatar ?: "")

            when(result) {
                is Result.Success -> setEffect { EditProfileContract.Effect.UserUpdatedSuccessfully(result.value) }
                is Result.Failure -> setEffect { EditProfileContract.Effect.UserUpdateFailure(result.throwable.message) }
            }

            setState { copy(applyChangedInProgress = false) }
        }
    }
}
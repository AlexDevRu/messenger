package com.example.chat.ui.edit_profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.auth.AuthVM
import com.example.chat.ui.auth.TextFieldVM
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.base.composables.CustomImageVM
import com.example.chat.ui.validation.InputValidator
import com.example.data.mappers.toDomainModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.UpdateUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
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

    private val client = ChatClient.instance()

    init {
        //loadCurrentUser()
    }

    private val userNameValidators = listOf(
        InputValidator.LessCharactersValidator(minCharacters)
    )

    val customImageVM = CustomImageVM(null)
    val userNameInputState = TextFieldVM(userNameValidators, maxCharacters)


    /*private fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val request = QueryUsersRequest(
                filter = Filters.autocomplete("id", Firebase.auth.currentUser!!.uid),
                offset = 0,
                limit = 1,
            )
            val userResult = client.queryUsers(request).execute()
            if(userResult.isSuccess) {
                val user = userResult.data().first()
                Log.e("asd", "CURRENT USER LOADED $user")
                user.name = Firebase.auth.currentUser?.displayName.orEmpty()
                user.image = Firebase.auth.currentUser!!.toDomainModel().getAvatarOrDefault()
                setState { copy(
                    userName = user.name,
                    avatar = user.image
                ) }
                Log.e("asd", "CURRENT USER AVTAR ${currentState.avatar}")
            }
        }
    }*/

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
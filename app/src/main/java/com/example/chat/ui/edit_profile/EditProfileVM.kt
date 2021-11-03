package com.example.chat.ui.edit_profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.validation.InputValidator
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.UpdateUserUseCase
import com.google.firebase.storage.FirebaseStorage
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class EditProfileVM(
    private val context: Application,
    private val updateUserUseCase: UpdateUserUseCase
): BaseViewModel<EditProfileContract.Event, EditProfileContract.State, EditProfileContract.Effect>() {

    companion object {
        private const val TAG = "EditProfileVM"
        private const val minCharacters = 4
        const val maxCharacters = 20
    }



    private val client = ChatClient.instance()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {

    }

    override fun createInitialState(): EditProfileContract.State {
        val currentUser = ChatClient.instance().getCurrentUser()!!
        return EditProfileContract.State(
            userName = currentUser.name,
            userNameValidationError = null,
            phone = currentUser.extraData["phone"]?.toString() ?: "",
            phoneValidationError = null,
            applyChangedInProgress = false,
            avatar = currentUser.image
        )
    }

    override fun handleEvent(event: EditProfileContract.Event) {
        when(event) {
            EditProfileContract.Event.OnApplyChanges -> applyChanges()
            is EditProfileContract.Event.OnFirstNameChanged -> validateFirstName(event.firstName)
            is EditProfileContract.Event.OnPhoneChanged -> setState { copy(phone = event.phone) }
            is EditProfileContract.Event.OnImageUpload -> uploadImageBitmap(event.data)
        }
    }

    private fun applyChanges() {
        val currentUser = client.getCurrentUser()!!
        setState { copy(applyChangedInProgress = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = updateUserUseCase(currentUser.id, currentState.userName, currentState.phone, currentState.avatar)

            when(result) {
                is Result.Success -> setEffect { EditProfileContract.Effect.UserUpdatedSuccessfully(result.value) }
                is Result.Failure -> setEffect { EditProfileContract.Effect.UserUpdateFailure(result.throwable.message) }
            }

            setState { copy(applyChangedInProgress = false) }
        }
    }

    /*private fun updateUserWithFields(setFields: Map<String, Any>) {
        val currentUser = client.getCurrentUser()!!

        val result = client.partialUpdateUser(currentUser.id, setFields).execute()

        if(result.isSuccess) {
            Log.d(TAG, "user is updated successfully ${result.data()}")
            //currentUser.name = result.data().name
            //currentUser.image = result.data().image
            setEffect { EditProfileContract.Effect.UserUpdatedSuccessfully(result.data()) }
        } else {
            Log.d(TAG, "user update error ${result.error().message}")
            setEffect { EditProfileContract.Effect.UserUpdateFailure(result.error().message) }
        }
    }*/

    private fun validateFirstName(firstName: String) {
        setState { copy(userName = firstName) }
        val validator = InputValidator.LessCharactersValidator(minCharacters)

        if(validator.validate(firstName)) {
            setState { copy(userNameValidationError = null) }
        } else {
            setState { copy(userNameValidationError = listOf(validator)) }
        }
    }

    private fun uploadImageBitmap(imageData: Any) {
        setState { copy(avatar = imageData) }
    }

}
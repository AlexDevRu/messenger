package com.example.chat.ui.edit_profile

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.validation.InputValidationError
import com.google.firebase.storage.FirebaseStorage
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class EditProfileVM(
    private val context: Application
): BaseViewModel<EditProfileContract.Event, EditProfileContract.State, EditProfileContract.Effect>() {

    companion object {
        private const val TAG = "EditProfileVM"
        private const val minCharacters = 4
    }

    val defaultAvatar: String
        get() = "https://getstream.imgix.net/images/random_svg/${currentState.userName.firstOrNull()?.uppercase()}.svg"

    private val client = ChatClient.instance()

    override fun createInitialState(): EditProfileContract.State {
        val currentUser = ChatClient.instance().getCurrentUser()!!
        return EditProfileContract.State(
            userName = currentUser.id,
            firstName = currentUser.name,
            firstNameValidationError = null,
            applyChangedInProgress = false,
            avatar = currentUser.image
        )
    }

    override fun handleEvent(event: EditProfileContract.Event) {
        when(event) {
            EditProfileContract.Event.OnApplyChanges -> applyChanges()
            is EditProfileContract.Event.OnFirstNameChanged -> validateFirstName(event.firstName)
            is EditProfileContract.Event.OnImageUpload -> uploadImageBitmap(event.data)
        }
    }

    private fun applyChanges() {
        val currentUser = client.getCurrentUser()!!
        setState { copy(applyChangedInProgress = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val setFields = mutableMapOf(
                "name" to currentState.firstName
            )

            when(currentState.avatar) {
                is String -> {
                    if((currentState.avatar as String).isNotEmpty()) {
                        setFields["image"] = currentState.avatar as String
                    }
                }
                is Uri -> {

                    val rootRef = FirebaseStorage.getInstance().reference
                    val feedbackRef = rootRef.child("user_avatars")

                    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(
                        currentState.avatar as Uri
                    ))

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val data = baos.toByteArray()

                    try {
                        val result = feedbackRef.child(currentUser.id).putBytes(data).await()
                        val uriResult = result.metadata?.reference?.downloadUrl?.await()
                        if(uriResult != null) {
                            setFields["image"] = uriResult.toString()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "avatar uri exception ${e.message}")
                    }
                    /*

                    val baos = ByteArrayOutputStream()

                    val bitmap = currentState.avatar as Bitmap

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val data = baos.toByteArray()

                    try {
                        val result = feedbackRef.child(currentUser.id).putBytes(data).await()
                        val uriResult = result.metadata?.reference?.downloadUrl?.await()
                        if(uriResult != null) {
                            setFields["image"] = uriResult.toString()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "avatar uri exception ${e.message}")
                    }*/
                }
            }

            updateUserWithFields(setFields)

            setState { copy(applyChangedInProgress = false) }
        }
    }

    private fun updateUserWithFields(setFields: Map<String, Any>) {
        val currentUser = client.getCurrentUser()!!

        val result = client.partialUpdateUser(currentUser.id, setFields).execute()

        if(result.isSuccess) {
            Log.d(TAG, "user is updated successfully ${result.data()}")
            currentUser.name = result.data().name
            currentUser.image = result.data().image
            setEffect { EditProfileContract.Effect.UserUpdatedSuccessfully(result.data()) }
        } else {
            Log.d(TAG, "user update error ${result.error().message}")
            setEffect { EditProfileContract.Effect.UserUpdateFailure(result.error().message) }
        }
    }

    private fun validateFirstName(firstName: String) {
        setState { copy(firstName = firstName) }
        val validator = InputValidationError.LessCharactersError(minCharacters)

        if(validator.validate(firstName)) {
            setState { copy(firstNameValidationError = null) }
        } else {
            setState { copy(firstNameValidationError = listOf(validator)) }
        }
    }

    private fun uploadImageBitmap(imageData: Any) {
        setState { copy(avatar = imageData) }
    }

}
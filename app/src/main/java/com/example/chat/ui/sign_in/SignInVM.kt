package com.example.chat.ui.sign_in

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.R
import com.example.chat.ui.base.BaseViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInVM: BaseViewModel<SignInContract.Event, SignInContract.State, SignInContract.Effect>() {

    private val client = ChatClient.instance()

    override fun createInitialState(): SignInContract.State {
        return SignInContract.State(
            userName = null,
            userNameValidationError = null,
            firstName = null,
            firstNameValidationError = null,
            loading = false
        )
    }

    override fun handleEvent(event: SignInContract.Event) {
        when (event) {
            is SignInContract.Event.OnSignInClicked -> authentificateUser()
            is SignInContract.Event.OnValidateUserName -> validateUserName(event.userName)
            is SignInContract.Event.OnValidateFirstName -> validateFirstName(event.firstName)
        }
    }

    private fun authentificateUser() {
        setState { copy(loading = true) }

        Log.d("asd", currentState.userName!!)
        Log.d("asd", currentState.firstName!!)

        val user = User(
            id = currentState.userName!!,
            extraData = mutableMapOf(
                "name" to currentState.firstName!!
            )
        )
        val token = client.devToken(user.id)

        client.connectUser(user, token).enqueue { result ->
            if (result.isSuccess) {
                setEffect { SignInContract.Effect.SignInSuccess }
            } else {
                setEffect { SignInContract.Effect.SignInFailure(result.error().message) }
            }
            setState { copy(loading = false) }
        }
    }

    private fun validateUserName(userName: String) {
        setState { copy(userName = userName) }
        if(userName.length < 4) {
            setState { copy(userNameValidationError = "Error username") }
        } else {
            setState { copy(userNameValidationError = null) }
        }
    }

    private fun validateFirstName(firstName: String) {
        setState { copy(firstName = firstName) }
        if(firstName.length < 4) {
            setState { copy(firstNameValidationError = "Error username") }
        } else {
            setState { copy(firstNameValidationError = null) }
        }
    }

}
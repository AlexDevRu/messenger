package com.example.chat.ui.sign_in

import android.util.Log
import com.example.chat.ui.base.BaseViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User

class SignInVM: BaseViewModel<SignInContract.Event, SignInContract.State, SignInContract.Effect>() {

    private val client = ChatClient.instance()

    companion object {
        private const val minCharacters = 4
    }

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
            id = currentState.userName!!.trim(),
            extraData = mutableMapOf(
                "name" to currentState.firstName!!.trim()
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
        if(userName.length in 1 until minCharacters) {
            setState { copy(userNameValidationError = SignInContract.InputValidationError.LessCharactersException(minCharacters)) }
        } else {
            setState { copy(userNameValidationError = null) }
        }
    }

    private fun validateFirstName(firstName: String) {
        setState { copy(firstName = firstName) }
        if(firstName.length in 1 until minCharacters) {
            setState { copy(firstNameValidationError = SignInContract.InputValidationError.LessCharactersException(minCharacters)) }
        } else {
            setState { copy(firstNameValidationError = null) }
        }
    }

}
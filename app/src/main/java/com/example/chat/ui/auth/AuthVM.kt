package com.example.chat.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.validation.InputValidationError
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.SignInUserUseCase
import com.example.domain.use_cases.remote.SignUpUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthVM(
    private val signInUserUseCase: SignInUserUseCase,
    private val signUpUserUseCase: SignUpUserUseCase
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    companion object {
        private const val TAG = "SignInVM"
        private const val minCharacters = 4
        const val maxCharachters = 20
    }

    override fun createInitialState(): AuthContract.State {
        return AuthContract.State(
            userName = null,
            userNameValidationError = emptyList(),
            password = null,
            passwordValidationError = emptyList(),
            saveSignInStatus = true,
            loading = false
        )
    }

    override fun handleEvent(event: AuthContract.Event) {
        when (event) {
            AuthContract.Event.OnSignInClicked -> authentificateUser()
            is AuthContract.Event.OnValidateUserName -> validateUserName(event.userName)
            is AuthContract.Event.OnValidatePassword -> validatePassword(event.password)
            is AuthContract.Event.OnSignInStatusChanged -> setState { copy(saveSignInStatus = event.status) }
            AuthContract.Event.OnModeChanged -> setState { copy(mode = currentState.mode.toggle()) }
        }
    }

    private fun authentificateUser() {
        setState { copy(loading = true) }

        Log.d(TAG, currentState.userName!!)
        Log.d(TAG, currentState.password!!)

        val userId = currentState.userName!!.trim()

        viewModelScope.launch(Dispatchers.IO) {

            val result = if(currentState.mode == AuthContract.SIGN_MODE.SIGN_IN) {
                signInUserUseCase(userId, currentState.password!!, currentState.saveSignInStatus)
            } else {
                signUpUserUseCase(userId, currentState.password!!, currentState.saveSignInStatus)
            }

            when(result) {
                is Result.Success -> setEffect { AuthContract.Effect.SignInSuccess }
                is Result.Failure -> setEffect { AuthContract.Effect.SignInFailure(result.throwable) }
            }

            setState { copy(loading = false) }
        }
    }

    private fun validateUserName(userName: String) {
        setState { copy(userName = userName) }

        val validator = InputValidationError.LessCharactersError(minCharacters)

        if(validator.validate(userName)) {
            setState { copy(userNameValidationError = null) }
        } else {
            setState { copy(userNameValidationError = listOf(validator)) }
        }
    }

    private fun validatePassword(password: String) {
        setState { copy(password = password) }

        val errors = listOf(
            InputValidationError.LessCharactersError(minCharacters),
            InputValidationError.NoNumbersError,
            InputValidationError.SameCaseError
        ).filter { !it.validate(password) }

        if(errors.isNotEmpty()) {
            setState { copy(passwordValidationError = errors) }
        } else {
            setState { copy(passwordValidationError = null) }
        }
    }

}
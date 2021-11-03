package com.example.chat.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.validation.InputValidator
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
            email = null,
            emailValidationError = emptyList(),
            password = null,
            passwordValidationError = emptyList(),
            rememberMe = true,
            loading = false
        )
    }

    override fun handleEvent(event: AuthContract.Event) {
        when (event) {
            AuthContract.Event.OnSignInClicked -> authentificateUser()
            is AuthContract.Event.OnValidateEmail -> validateEmail(event.email)
            is AuthContract.Event.OnValidatePassword -> validatePassword(event.password)
            is AuthContract.Event.OnSignInStatusChanged -> setState { copy(rememberMe = event.status) }
            AuthContract.Event.OnModeChanged -> setState { copy(mode = currentState.mode.toggle()) }
        }
    }

    private fun authentificateUser() {
        setState { copy(loading = true) }

        Log.d(TAG, currentState.email!!)
        Log.d(TAG, currentState.password!!)

        val email = currentState.email!!.trim()

        viewModelScope.launch(Dispatchers.IO) {

            val isSignIn = currentState.mode == AuthContract.SIGN_MODE.SIGN_IN
            val result = if(isSignIn) {
                signInUserUseCase(email, currentState.password!!, currentState.rememberMe)
            } else {
                signUpUserUseCase(email, currentState.password!!, currentState.rememberMe)
            }

            when(result) {
                is Result.Success -> setEffect { if(isSignIn) AuthContract.Effect.SignInSuccess else AuthContract.Effect.SignUpSuccess }
                is Result.Failure -> setEffect { AuthContract.Effect.AuthFailure(result.throwable) }
            }

            setState { copy(loading = false) }
        }
    }

    private fun validateEmail(email: String) {
        setState { copy(email = email) }

        val validator = InputValidator.EmailValidator

        if(validator.validate(email)) {
            setState { copy(emailValidationError = null) }
        } else {
            setState { copy(emailValidationError = listOf(validator)) }
        }
    }

    private fun validatePassword(password: String) {
        setState { copy(password = password) }

        val errors = listOf(
            InputValidator.LessCharactersValidator(minCharacters),
            InputValidator.NoNumbersValidator,
            InputValidator.SameCaseValidator
        ).filter { !it.validate(password) }

        if(errors.isNotEmpty()) {
            setState { copy(passwordValidationError = errors) }
        } else {
            setState { copy(passwordValidationError = null) }
        }
    }

}
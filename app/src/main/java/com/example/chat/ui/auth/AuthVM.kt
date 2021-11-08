package com.example.chat.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.chat.ui.base.composables.TextFieldVM
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
        private const val maxCharacters = 20
    }

    private val emailValidators = listOf(
        InputValidator.EmailValidator
    )

    private val userNameValidators = listOf(
        InputValidator.LessCharactersValidator(minCharacters)
    )

    private val passwordValidators = listOf(
        InputValidator.LessCharactersValidator(minCharacters),
        InputValidator.NoNumbersValidator,
        InputValidator.SameCaseValidator
    )

    val emailInputState = TextFieldVM(emailValidators)
    val userNameInputState = TextFieldVM(userNameValidators, maxCharacters)
    val passwordInputState = TextFieldVM(passwordValidators)

    override fun createInitialState(): AuthContract.State {
        return AuthContract.State(
            rememberMe = true,
            loading = false
        )
    }

    override fun handleEvent(event: AuthContract.Event) {
        when (event) {
            AuthContract.Event.OnSignInClicked -> authentificateUser()
            is AuthContract.Event.OnSignInStatusChanged -> setState { copy(rememberMe = event.status) }
            AuthContract.Event.OnModeChanged -> setState { copy(mode = currentState.mode.toggle()) }
        }
    }

    private fun authentificateUser() {
        setState { copy(loading = true) }

        val email = emailInputState.value
        val userName = userNameInputState.value
        val password = passwordInputState.value

        Log.d(TAG, "email $email")
        Log.d(TAG, "userName $userName")
        Log.d(TAG, "password $password")

        viewModelScope.launch(Dispatchers.IO) {

            val isSignIn = currentState.mode == AuthContract.SIGN_MODE.SIGN_IN
            val result = if(isSignIn) {
                signInUserUseCase(email, password, currentState.rememberMe)
            } else {
                signUpUserUseCase(email, password, userName, currentState.rememberMe)
            }

            when(result) {
                is Result.Success -> setEffect {
                    if(isSignIn) AuthContract.Effect.SignInSuccess(result.value)
                    else AuthContract.Effect.SignUpSuccess(result.value)
                }
                is Result.Failure -> setEffect { AuthContract.Effect.AuthFailure(result.throwable) }
            }

            setState { copy(loading = false) }
        }
    }
}
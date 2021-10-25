package com.example.chat.ui.sign_in

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.SignInUserUseCase
import com.example.domain.use_cases.remote.SignUpUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInVM(
    private val signInUserUseCase: SignInUserUseCase,
    private val signUpUserUseCase: SignUpUserUseCase
) : BaseViewModel<SignInContract.Event, SignInContract.State, SignInContract.Effect>() {

    companion object {
        private const val TAG = "SignInVM"
        private const val minCharacters = 4
    }

    override fun createInitialState(): SignInContract.State {
        return SignInContract.State(
            userName = null,
            userNameValidationError = null,
            password = null,
            passwordValidationError = null,
            saveSignInStatus = true,
            loading = false
        )
    }

    override fun handleEvent(event: SignInContract.Event) {
        when (event) {
            SignInContract.Event.OnSignInClicked -> authentificateUser()
            is SignInContract.Event.OnValidateUserName -> validateUserName(event.userName)
            is SignInContract.Event.OnValidatePassword -> validatePassword(event.password)
            is SignInContract.Event.OnSignInStatusChanged -> setState { copy(saveSignInStatus = event.status) }
            SignInContract.Event.OnModeChanged -> setState { copy(mode = currentState.mode.toggle()) }
        }
    }

    private fun authentificateUser() {
        setState { copy(loading = true) }

        Log.d(TAG, currentState.userName!!)
        Log.d(TAG, currentState.password!!)

        val userId = currentState.userName!!.trim()

        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loading = true) }

            val result = if(currentState.mode == SignInContract.SIGN_MODE.SIGN_IN) {
                signInUserUseCase(userId, currentState.password!!, currentState.saveSignInStatus)
            } else {
                signUpUserUseCase(userId, currentState.password!!, currentState.saveSignInStatus)
            }

            setState { copy(loading = false) }

            when(result) {
                is Result.Success -> setEffect { SignInContract.Effect.SignInSuccess }
                is Result.Failure -> setEffect { SignInContract.Effect.SignInFailure(result.throwable) }
            }
        }
    }

    private fun validateUserName(userName: String) {
        setState { copy(userName = userName) }

        val validator = SignInContract.InputValidationError.LessCharactersError(minCharacters)

        if(validator.validate(userName)) {
            setState { copy(userNameValidationError = null) }
        } else {
            setState { copy(userNameValidationError = listOf(validator)) }
        }
    }

    private fun validatePassword(password: String) {
        setState { copy(password = password) }

        val errors = listOf(
            SignInContract.InputValidationError.LessCharactersError(minCharacters),
            SignInContract.InputValidationError.NoNumbersError,
            SignInContract.InputValidationError.SameCaseError
        ).filter { !it.validate(password) }

        if(errors.isNotEmpty()) {
            setState { copy(passwordValidationError = errors) }
        } else {
            setState { copy(passwordValidationError = null) }
        }
    }

}
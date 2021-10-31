package com.example.chat.ui.auth

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.chat.R
import com.example.chat.ui.base.composables.CheckboxWithText
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
private fun Logo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(128.dp, 128.dp)) {
            Image(
                painter = painterResource(R.drawable.speak),
                contentDescription = "logo"
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 28.sp)
    }
}

@Composable
fun AuthScreen(
    navigateToMain: () -> Unit,
    viewModel: AuthVM = getViewModel()
){

    val scrollState = rememberScrollState()

    val authState by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val constraintSet = ConstraintSet {
        val logo = createRefFor("logo")
        val usernameInput = createRefFor("usernameInput")
        val passwordInput = createRefFor("passwordInput")
        val rememberMeCheckbox = createRefFor("rememberMeCheckbox")
        val signButton = createRefFor("signButton")
        val toggleModeButton = createRefFor("toggleModeButton")

        constrain(logo) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(usernameInput) {
            top.linkTo(logo.bottom, margin = 30.dp)
        }
        constrain(passwordInput) {
            top.linkTo(usernameInput.bottom)
        }
        constrain(rememberMeCheckbox) {
            top.linkTo(passwordInput.bottom, margin = 8.dp)
            start.linkTo(parent.start)
        }
        constrain(signButton) {
            top.linkTo(rememberMeCheckbox.bottom, margin = 16.dp)
        }
        constrain(toggleModeButton) {
            top.linkTo(signButton.bottom, margin = 8.dp)
        }
    }

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) {

        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                AuthContract.Effect.SignInSuccess -> {
                    navigateToMain()
                    Log.d("asd", "navigateToMain")
                }
                is AuthContract.Effect.SignInFailure -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as AuthContract.Effect.SignInFailure)
                                    .throwable?.message.orEmpty()
                            )
                    }
                }
            }
        })

        ConstraintLayout(modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .verticalScroll(scrollState),
            constraintSet = constraintSet
        ) {

            Logo(modifier = Modifier.layoutId("logo"))

            TextInputField(
                modifier = Modifier
                    .layoutId("usernameInput")
                    .fillMaxWidth(),
                label = R.string.username,
                value = authState.userName.orEmpty(),
                maxCount = AuthVM.maxCharachters,
                errors = authState.userNameValidationError,
                onValueChanged = {
                    viewModel.setEvent(AuthContract.Event.OnValidateUserName(it))
                }
            )

            TextInputField(
                modifier = Modifier
                    .layoutId("passwordInput")
                    .fillMaxWidth(),
                label = R.string.password,
                transformation = PasswordVisualTransformation(),
                value = authState.password.orEmpty(),
                errors = authState.passwordValidationError,
                onValueChanged = {
                    viewModel.setEvent(AuthContract.Event.OnValidatePassword(it))
                }
            )

            CheckboxWithText(
                modifier = Modifier.layoutId("rememberMeCheckbox"),
                text = stringResource(id = R.string.remember_me),
                enabled = !authState.loading,
                checked = authState.saveSignInStatus,
                onCheckedChanged = {
                    viewModel.setEvent(AuthContract.Event.OnSignInStatusChanged(it))
                }
            )

            ProgressButton(
                modifier = Modifier.layoutId("signButton")
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnSignInClicked)
                },
                loading = authState.loading,
                enabled = authState.userNameValidationError.isNullOrEmpty() &&
                        authState.passwordValidationError.isNullOrEmpty() &&
                        !authState.password.isNullOrEmpty() && !authState.userName.isNullOrEmpty(),
                textRes = if(authState.mode == AuthContract.SIGN_MODE.SIGN_IN) R.string.sign_in else R.string.sign_up
            )

            OutlinedButton(
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnModeChanged)
                },
                enabled = !authState.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("toggleModeButton")
            ) {
                val textRes = if(authState.mode == AuthContract.SIGN_MODE.SIGN_IN) R.string.sign_in_button_text else R.string.sign_up_button_text
                Text(text = stringResource(textRes), modifier = Modifier.padding(8.dp))
            }
        }
    }
}

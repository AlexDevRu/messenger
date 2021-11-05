package com.example.chat.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import com.example.chat.ui.base.composables.CheckboxWithText
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.ui.phone.PhoneScreen
import com.example.domain.exceptions.WrongCredentialsException
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
private fun Logo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.size(128.dp, 128.dp),
            painter = painterResource(R.drawable.speak),
            contentDescription = "logo"
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 28.sp)
    }
}

@Composable
fun Auth(
    navigateToMain: () -> Unit
){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "check_credentials") {
        composable("check_credentials") {
            AuthScreen(
                onSignInSuccess = navigateToMain,
                onSignUpSuccess = { navController.navigate("new_user_settings") }
            )
        }
        composable("new_user_settings") {
            NewAccountSettingsScreen(onSuccess = { navController.navigate("phone") } )
        }
        composable("phone") {
            PhoneScreen(onSkip = navigateToMain, onSuccess = navigateToMain, cancelLabel = R.string.skip)
        }
    }
}

@Composable
fun AuthScreen(
    onSignInSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthVM = getViewModel()
) {
    val scrollState = rememberScrollState()

    val authState by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val constraintSet = ConstraintSet {
        val logo = createRefFor("logo")
        val emailInput = createRefFor("emailInput")
        val passwordInput = createRefFor("passwordInput")
        val rememberMeCheckbox = createRefFor("rememberMeCheckbox")
        val signButton = createRefFor("signButton")
        val toggleModeButton = createRefFor("toggleModeButton")

        constrain(logo) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(emailInput) {
            top.linkTo(logo.bottom, margin = 30.dp)
        }
        constrain(passwordInput) {
            top.linkTo(emailInput.bottom)
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

        val wrongCredentialsMessage = stringResource(R.string.wrong_credentials_message)

        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                AuthContract.Effect.SignInSuccess -> {
                    onSignInSuccess()
                }
                AuthContract.Effect.SignUpSuccess -> {
                    onSignUpSuccess()
                }
                is AuthContract.Effect.AuthFailure -> {

                    val throwable = (effect as AuthContract.Effect.AuthFailure).throwable

                    val message = when(throwable) {
                        is WrongCredentialsException -> wrongCredentialsMessage
                        else -> throwable?.message.orEmpty()
                    }

                    Log.e("asd", message)

                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(message)
                    }
                }
            }
        })

        ConstraintLayout(modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState),
            constraintSet = constraintSet
        ) {

            Logo(modifier = Modifier.layoutId("logo"))

            TextInputField(
                modifier = Modifier
                    .layoutId("emailInput")
                    .fillMaxWidth(),
                label = R.string.email,
                value = authState.email.orEmpty(),
                errors = authState.emailValidationError,
                onValueChanged = {
                    viewModel.setEvent(AuthContract.Event.OnValidateEmail(it))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            TextInputField(
                modifier = Modifier
                    .layoutId("passwordInput")
                    .fillMaxWidth(),
                label = R.string.password,
                transformation = PasswordVisualTransformation(),
                value = authState.password.orEmpty(),
                maxCount = AuthVM.maxCharachters,
                errors = authState.passwordValidationError,
                onValueChanged = {
                    viewModel.setEvent(AuthContract.Event.OnValidatePassword(it))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            )

            CheckboxWithText(
                modifier = Modifier.layoutId("rememberMeCheckbox"),
                text = stringResource(id = R.string.remember_me),
                enabled = !authState.loading,
                checked = authState.rememberMe,
                onCheckedChanged = {
                    viewModel.setEvent(AuthContract.Event.OnSignInStatusChanged(it))
                }
            )

            ProgressButton(
                modifier = Modifier
                    .layoutId("signButton")
                    .fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnSignInClicked)
                },
                loading = authState.loading,
                enabled = authState.emailValidationError.isNullOrEmpty() &&
                        authState.passwordValidationError.isNullOrEmpty() &&
                        !authState.password.isNullOrEmpty() && !authState.email.isNullOrEmpty(),
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
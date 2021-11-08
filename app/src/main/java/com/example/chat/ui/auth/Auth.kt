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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import com.example.chat.ui.base.composables.CheckboxWithText
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.ui.phone.PhoneScreen
import com.example.chat.utils.globalVM
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
            NewAvatarSignUpScreen(
                onSuccess = { navController.navigate("phone") },
                onSkip = { navController.navigate("phone") }
            )
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
    val globalVM = LocalContext.current.globalVM()

    val scrollState = rememberScrollState()

    val userNameHasErrors by viewModel.userNameInputState.hasErrorsOrEmpty.collectAsState()
    val passwordHasErrors by viewModel.passwordInputState.hasErrorsOrEmpty.collectAsState()
    val emailHasErrors by viewModel.emailInputState.hasErrorsOrEmpty.collectAsState()

    val authState by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) {

        val wrongCredentialsMessage = stringResource(R.string.wrong_credentials_message)

        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                is AuthContract.Effect.SignInSuccess -> {
                    Log.d("asd", "SIGN IN USER ${(effect as AuthContract.Effect.SignInSuccess).user}")
                    globalVM.reloadCurrentUser()
                    onSignInSuccess()
                }
                is AuthContract.Effect.SignUpSuccess -> {
                    Log.d("asd", "SIGN UP USER ${(effect as AuthContract.Effect.SignUpSuccess).user}")
                    globalVM.reloadCurrentUser()
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

        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Logo()

            Spacer(modifier = Modifier.height(16.dp))

            if(authState.mode == AuthContract.SIGN_MODE.SIGN_UP) {
                TextInputField(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.username,
                    textFieldVM = viewModel.userNameInputState,
                    enabled = !authState.loading
                )
            }

            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.email,
                textFieldVM = viewModel.emailInputState,
                enabled = !authState.loading,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.password,
                transformation = PasswordVisualTransformation(),
                textFieldVM = viewModel.passwordInputState,
                enabled = !authState.loading,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                CheckboxWithText(
                    text = stringResource(id = R.string.remember_me),
                    enabled = !authState.loading,
                    checked = authState.rememberMe,
                    onCheckedChanged = {
                        viewModel.setEvent(AuthContract.Event.OnSignInStatusChanged(it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnSignInClicked)
                },
                loading = authState.loading,
                enabled = !(emailHasErrors && userNameHasErrors && passwordHasErrors),
                textRes = if(authState.mode == AuthContract.SIGN_MODE.SIGN_IN) R.string.sign_in else R.string.sign_up
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnModeChanged)
                },
                enabled = !authState.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                val textRes = if(authState.mode == AuthContract.SIGN_MODE.SIGN_IN) R.string.sign_in_button_text else R.string.sign_up_button_text
                Text(text = stringResource(textRes), modifier = Modifier.padding(8.dp))
            }
        }
    }
}
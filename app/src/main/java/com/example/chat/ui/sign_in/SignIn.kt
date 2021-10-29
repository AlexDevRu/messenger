package com.example.chat.ui.sign_in

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.chat.R
import com.example.chat.ui.main.Screen
import com.example.chat.ui.validation.InputValidationError
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

data class StringResWrapper(@StringRes val stringRes: Int, val formatArgs: Array<Any> = emptyArray())



@Composable
private fun TextInputField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    transformation: VisualTransformation = VisualTransformation.None,
    value: String = "",
    errors: List<InputValidationError>? = null,
    maxCount: Int = 20,
    enabled: Boolean = true,
    onValueChanged: (String) -> Unit = {}
) {

    val labelStr = stringResource(label)

    val errorStr: (InputValidationError) -> StringResWrapper = err@ {
        return@err when(it) {
            is InputValidationError.LessCharactersError -> StringResWrapper(R.string.small_length_validation, arrayOf(labelStr, 4))
            InputValidationError.NoNumbersError -> StringResWrapper(R.string.password_no_numbers_validation)
            InputValidationError.SameCaseError -> StringResWrapper(R.string.password_same_case_validation)
        }
    }

    val constraintSet = ConstraintSet {
        val fieldRef = createRefFor("field")
        val errorsRef = createRefFor("errors")
        val counterRef = createRefFor("counter")

        constrain(fieldRef) {
            top.linkTo(parent.top)
        }

        constrain(errorsRef) {
            top.linkTo(fieldRef.bottom)
            start.linkTo(fieldRef.start)
            end.linkTo(counterRef.start)
            width = Dimension.fillToConstraints
        }

        constrain(counterRef) {
            top.linkTo(fieldRef.bottom)
            end.linkTo(fieldRef.end)
        }
    }

    val errorIcon: @Composable () -> Unit = { Icon(
        painter = painterResource(id = R.drawable.ic_baseline_error_24),
        tint = Color.Red,
        contentDescription = ""
    ) }

    ConstraintLayout(constraintSet, modifier = modifier.padding( vertical = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if(it.length <= maxCount)
                    onValueChanged(it)
            },
            label = { Text(text = labelStr) },
            enabled = enabled,
            placeholder = { Text(text = stringResource(id = label)) },
            isError = !errors.isNullOrEmpty(),
            leadingIcon = leadingIcon,
            trailingIcon = if(errors.isNullOrEmpty()) trailingIcon else errorIcon,
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("field"),
            visualTransformation = transformation,
        )
        Column(modifier = Modifier
            .padding(end = 10.dp)
            .layoutId("errors")
        ) {
            errors?.forEach {
                val stringRes = errorStr(it)
                Text(
                    text = stringResource(id = stringRes.stringRes, *(stringRes.formatArgs)),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = "${value.length} / $maxCount",
            modifier = Modifier.layoutId("counter")
        )
    }
}


@Composable
fun AuthScreen(
    navigateToMain: () -> Unit,
    viewModel: SignInVM = getViewModel()
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
            top.linkTo(usernameInput.bottom, margin = 8.dp)
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

        when(effect) {
            AuthContract.Effect.SignInSuccess -> {
                navigateToMain()
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

        ConstraintLayout(modifier = Modifier
            .padding(16.dp).background(Color.White)
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
                maxCount = SignInVM.maxCharachters,
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
                modifier = Modifier
                    .layoutId("rememberMeCheckbox"),
                text = stringResource(id = R.string.remember_me),
                enabled = !authState.loading,
                checked = authState.saveSignInStatus,
                onCheckedChanged = {
                    viewModel.setEvent(AuthContract.Event.OnSignInStatusChanged(it))
                }
            )

            Button(
                onClick = {
                    viewModel.setEvent(AuthContract.Event.OnSignInClicked)
                },
                enabled = !authState.loading &&
                        authState.userNameValidationError.isNullOrEmpty() &&
                        authState.passwordValidationError.isNullOrEmpty() &&
                        !authState.password.isNullOrEmpty() && !authState.userName.isNullOrEmpty()
                ,
                modifier = Modifier
                    .layoutId("signButton")
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .background(colorResource(R.color.purple_700)),
            ) {
                val textRes = if(authState.mode == AuthContract.SIGN_MODE.SIGN_IN) R.string.sign_in else R.string.sign_up
                if(authState.loading) CircularProgressIndicator(modifier = Modifier.size(24.dp, 24.dp))
                Text(text = stringResource(textRes), modifier = Modifier.padding(8.dp))
            }

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

@Composable
fun CheckboxWithText(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChanged: (Boolean) -> Unit = {}
) {
    Row(
        modifier = modifier.clickable {
            if(enabled) onCheckedChanged(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(checked = checked, enabled = enabled, onCheckedChange = {
            onCheckedChanged(it)
        })

        Text(text = text)
    }
}
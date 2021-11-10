package com.example.chat.ui.phone

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
private fun Preview() {
    PhoneScreen({}, {}, R.string.cancel)
}


@Composable
fun PhoneScreen(
    onSkip: () -> Unit,
    onSuccess: () -> Unit,
    @StringRes cancelLabel: Int,
    viewModel: PhoneVM = getViewModel()
) {

    val navController = rememberNavController()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        LaunchedEffect(key1 = effect) {
            when(effect) {
                PhoneContract.Effect.SmsSended -> {
                    navController.navigate("check_sms_code")
                }
                is PhoneContract.Effect.PhoneSaveFailure -> {
                    val errorMessage = (effect as PhoneContract.Effect.PhoneSaveFailure).message.orEmpty()
                    Log.d("asd", errorMessage)
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                    }
                }
                is PhoneContract.Effect.PhoneSavedSuccessfully -> {
                    Log.e("asd", "phone sms success")
                    onSuccess()
                }
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp, 24.dp, 16.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.ic_baseline_phone_24),
                tint = MaterialTheme.colors.onSurface,
                contentDescription = ""
            )

            Text(
                stringResource(R.string.link_your_account_with_phone),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            NavHost(navController = navController, startDestination = "input_phone") {
                composable("input_phone") {
                    InputPhoneNumber(viewModel, onCancel =  onSkip, cancelLabel = cancelLabel)
                }
                composable("check_sms_code") {
                    CheckSmsCodeScreen(viewModel)
                }
            }
        }
    }
}

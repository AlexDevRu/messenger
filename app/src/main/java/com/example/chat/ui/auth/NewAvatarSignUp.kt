package com.example.chat.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomImage
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.utils.globalVM
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
private fun Preview() {
    NewAvatarSignUpScreen({}, {})
}


@Composable
fun NewAvatarSignUpScreen(
    onSuccess: () -> Unit,
    onSkip: () -> Unit,
    viewModel: NewAvatarSignUpVM = getViewModel()
) {

    val globalVM = LocalContext.current.globalVM()
    val user by globalVM.user.collectAsState()

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    LaunchedEffect(key1 = Unit) {
        if(user != null) viewModel.setEvent(NewAvatarSignUpContract.Event.SetUserData(user!!))
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        LaunchedEffect(key1 = effect) {
            when(effect) {
                is NewAvatarSignUpContract.Effect.UploadSuccessfully -> {
                    globalVM.reloadCurrentUser()
                    onSuccess()
                }
                is NewAvatarSignUpContract.Effect.UploadFailure -> {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as NewAvatarSignUpContract.Effect.UploadFailure).message
                            )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                Text(text = stringResource(R.string.set_your_avatar), fontSize = 20.sp)

                Spacer(modifier = Modifier.height(20.dp))

                CustomImage(
                    customImageVM = viewModel.customImageVM,
                    clickable = !state.loading
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProgressButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setEvent(NewAvatarSignUpContract.Event.OnConfirm)
                    },
                    textRes = R.string.confirm,
                    loading = state.loading
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkip,
                    enabled = !state.loading
                ) {
                    Text(stringResource(R.string.skip), modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
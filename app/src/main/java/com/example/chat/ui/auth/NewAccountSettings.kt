package com.example.chat.ui.auth

import android.util.Log
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
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomImage
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.validation.InputValidator
import com.example.chat.utils.globalVM
import com.example.data.mappers.toDomainModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
private fun Preview() {
    NewAccountSettingsScreen({}, {}, ChatUser())
}


@Composable
fun NewAccountSettingsScreen(
    onSuccess: () -> Unit,
    onSkip: () -> Unit,
    chatUser: ChatUser,
    viewModel: NewAccountSettingsVM = getViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(NewAccountSettingsContract.Event.SetUserData(chatUser))
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        LaunchedEffect(key1 = effect) {
            when(effect) {
                is NewAccountSettingsContract.Effect.UploadSuccessfully -> {
                    val user = (effect as NewAccountSettingsContract.Effect.UploadSuccessfully).user
                    Log.e("asd", "UPLOADED USER $user")
                    val userr = ChatClient.instance().getCurrentUser()
                    Log.e("asd", "reconnected user $userr")
                    context.globalVM().setUser(userr?.toDomainModel())
                    onSuccess()
                }
                is NewAccountSettingsContract.Effect.UploadFailure -> {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as NewAccountSettingsContract.Effect.UploadFailure).message
                            )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CustomImage(
                    customImageVM = viewModel.customImageVM,
                    clickable = !state.loading
                )
                ProgressButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setEvent(NewAccountSettingsContract.Event.OnConfirm)
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

data class TextFieldState(
    val value: String = "",
    val valueErrors: List<InputValidator> = emptyList(),
)

class TextFieldVM(
    private val validators: List<InputValidator> = emptyList(),
    val maxCount: Int? = null
) {

    private val _state = MutableStateFlow(TextFieldState())
    val state: StateFlow<TextFieldState> = _state

    val value: String get() = state.value.value

    private val _hasErrors = MutableStateFlow(false)
    val hasErrors: StateFlow<Boolean> = _hasErrors

    private val _hasErrorsOrEmpty = MutableStateFlow(true)
    val hasErrorsOrEmpty: StateFlow<Boolean> = _hasErrorsOrEmpty

    //val hasErrors: Boolean get() = state.value.valueErrors.isEmpty()
    //val hasErrorsOrEmpty: Boolean get() = hasErrors || value.isEmpty()

    fun onValueChanged(newValue: String) {
        if(maxCount != null && newValue.length > maxCount)
            return

        _state.value = state.value.copy(value = newValue)

        val failedValidators = validators.filter {
            !it.validate(newValue)
        }

        _state.value = state.value.copy(
            valueErrors = failedValidators,
            value = newValue,
        )

        _hasErrors.value = failedValidators.isNotEmpty()
        _hasErrorsOrEmpty.value = hasErrors.value || newValue.isEmpty()
    }
}
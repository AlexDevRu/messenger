package com.example.chat.ui.edit_profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomImage
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.ui.base.composables.Toolbar
import com.example.chat.ui.models.Screen
import com.example.data.mappers.toDomainModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun EditProfileScreen(
    onCancel: () -> Unit = {},
    onSuccess: (ChatUser) -> Unit,
    hasToolbar: Boolean = true,
    viewModel: EditProfileVM = getViewModel()
) {

    val user by ChatDomain.instance().user.collectAsState()

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val userNameInvalid by viewModel.userNameInputState.hasErrorsOrEmpty.collectAsState()

    LaunchedEffect(key1 = Unit) {
        if(user != null) {
            viewModel.customImageVM.setUser(user!!.toDomainModel())
            viewModel.userNameInputState.onValueChanged(user!!.name)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if(hasToolbar) Toolbar(Screen.EditProfile.displayText, onCancel)
        }
    ) {
        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                is EditProfileContract.Effect.UserUpdatedSuccessfully -> {
                    onSuccess((effect as EditProfileContract.Effect.UserUpdatedSuccessfully).user)
                    onCancel()
                }
                is EditProfileContract.Effect.UserUpdateFailure -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as EditProfileContract.Effect.UserUpdateFailure)
                                    .message.orEmpty()
                            )
                    }
                }
            }
        })

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CustomImage(
                customImageVM = viewModel.customImageVM,
                clickable = !state.applyChangedInProgress
            )

            Spacer(modifier = Modifier.height(16.dp))

            /*TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.email,
                textFieldVM = viewModel.emailInputState,
                enabled = !state.applyChangedInProgress,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )*/

            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.username,
                textFieldVM = viewModel.userNameInputState,
                enabled = !state.applyChangedInProgress
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(EditProfileContract.Event.OnApplyChanges)
                },
                loading = state.applyChangedInProgress,
                enabled = !userNameInvalid,
                textRes = R.string.apply_changes
            )
        }
    }
}
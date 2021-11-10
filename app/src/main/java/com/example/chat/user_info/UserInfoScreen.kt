package com.example.chat.user_info

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.Toolbar
import com.example.chat.utils.toReadableString
import com.example.data.mappers.toDataModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.compose.ui.common.LoadingView
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun UserInfoScreen(
    user: ChatUser,
    onBackPressed: () -> Unit,
    viewModel: UserInfoVM = getViewModel()
) {

    val scrollState = rememberScrollState()

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(UserInfoContract.Event.OnGetUserPhoneById(user.id))
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if(it.values.all { it }) {
            viewModel.setEvent(UserInfoContract.Event.OnAddContact(user.userName))
        }
    }

    Scaffold(
        topBar = {
            Toolbar(title = user.userName, onBackPressed = onBackPressed)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().background(ChatTheme.colors.appBackground)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            UserAvatar(user = user.toDataModel(), modifier = Modifier.size(150.dp))

            Spacer(modifier = Modifier.height(20.dp))

            UserInfoRow(title = stringResource(R.string.was_online), value = user.lastActive.toReadableString())
            UserInfoRow(title = stringResource(R.string.email), value = user.email)
            UserInfoRow(title = stringResource(R.string.username), value = user.userName)

            if(user.phone != null || state.loading) {
                LoadingView(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp))
            } else {
                UserInfoRow(title = stringResource(R.string.phone), value = state.phone ?: "-")

                Spacer(modifier = Modifier.height(20.dp))

                ProgressButton(
                    modifier = Modifier.fillMaxWidth(),
                    loading = state.isContactInsertInProgress,
                    enabled = state.phone != null,
                    onClick = {
                        permissionsLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS))
                    },
                    textRes = R.string.add_to_contacts
                )
            }
        }
    }
}

@Composable
private fun UserInfoRow(
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(value)
    }
}
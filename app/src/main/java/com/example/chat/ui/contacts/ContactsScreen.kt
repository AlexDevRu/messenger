package com.example.chat.ui.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.chat.R
import com.example.chat.ui.base.composables.Toolbar
import io.getstream.chat.android.compose.ui.common.LoadingView
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun ContactsScreen(
    onBackPressed: () -> Unit,
    goToChannel: (cid: String) -> Unit
) {

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .background(ChatTheme.colors.appBackground),
        topBar = { Toolbar(R.string.contacts, onBackPressed) }
    ) {
        ContactsList(goToChannel, scaffoldState)
    }
}

@Composable
fun ContactsList(
    goToChannel: (cid: String) -> Unit,
    scaffoldState: ScaffoldState,
    viewModel: ContactsVM = getViewModel()
) {

    val snackbarCoroutineScope = rememberCoroutineScope()

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            viewModel.setEvent(ContactsContract.Event.OnReadContacts)
        }
    }

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)


    LaunchedEffect(key1 = effect) {
        when(effect) {
            is ContactsContract.Effect.GoToChat -> {
                val cid = (effect as ContactsContract.Effect.GoToChat).cid
                goToChannel(cid)
            }
            is ContactsContract.Effect.ShowErrorMessage -> {
                snackbarCoroutineScope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar(
                            (effect as ContactsContract.Effect.ShowErrorMessage).message
                        )
                }
            }
        }
    }


    if(state.loading) {
        LoadingView(modifier = Modifier.fillMaxSize())
    } else {
        if(state.contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.contacts_not_found))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ChatTheme.colors.appBackground)
            ) {
                state.contacts.forEach { contact ->
                    item {
                        ContactItem(
                            modifier = Modifier.fillMaxWidth(),
                            contact = contact,
                            onClick = {
                                if (contact.user?.id != null)
                                    viewModel.setEvent(ContactsContract.Event.OnUserClick(contact.user!!.id))
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit){
        permissionsLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}

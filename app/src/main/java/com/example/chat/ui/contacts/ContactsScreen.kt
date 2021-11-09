package com.example.chat.ui.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.Toolbar
import org.koin.androidx.compose.getViewModel

@Composable
fun ContactsScreen(
    onBackPressed: () -> Unit,
    goToChannel: (cid: String) -> Unit
) {
    Scaffold(
        topBar = { Toolbar(R.string.contacts, onBackPressed) }
    ) {
        ContactsList(goToChannel)
    }
}

@Composable
fun ContactsList(
    goToChannel: (cid: String) -> Unit,
    viewModel: ContactsVM = getViewModel()
) {

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
        }
    }


    if(state.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color.Blue)
        }
    }
    else {

        if(state.contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.contacts_not_found))
            }
        } else {
            LazyColumn() {
                state.contacts.forEach { contact ->
                    item {
                        ContactItem(
                            modifier = Modifier.fillMaxWidth(),
                            contact = contact,
                            onClick = {
                                if(contact.user?.id != null)
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

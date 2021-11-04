package com.example.chat.ui.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.R
import com.example.chat.ui.base.composables.Toolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.compose.getViewModel


@Preview
@Composable
private fun Preview() {
    ContactsScreen({})
}

@Composable
fun ContactsScreen(
    onBackPressed: () -> Unit,
    viewModel: ContactsVM = getViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            viewModel.setEvent(ContactsContract.Event.OnReadContacts)
        }
    }

    Scaffold(
        topBar = { Toolbar(R.string.contacts, onBackPressed) }
    ) {
        if(state.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color.Blue)
            }
        }
        else {

            if(state.contacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts found")
                }
            } else {
                LazyColumn() {
                    state.contacts.forEach { contact ->
                        item {
                            Contact(
                                modifier = Modifier.fillMaxWidth(),
                                contact = contact
                            ) {

                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit){
        permissionsLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}


@Composable
fun Contact(
    modifier: Modifier = Modifier,
    contact: Contact,
    onClick: () -> Unit
) {
    Box(modifier = modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple()
    ) {
        onClick()
    }) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //UserAvatar(user = user, modifier = Modifier.size(40.dp))
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.ic_channels),
                contentDescription = ""
            )
            Column() {
                Text(text = contact.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = contact.phoneNumber, fontSize = 12.sp)
                //Text(text = convertDate(user.lastActive!!.time), fontSize = 12.sp)
            }
        }
    }
}

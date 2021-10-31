package com.example.chat.ui.users

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chat.R
import com.example.chat.ui.main.MainContract
import com.example.chat.ui.models.Screen
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.SearchInput
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun UsersScreen(
    navController: NavController,
    viewModel: UsersVM = getViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            UsersToolbar(
                query = state.query.orEmpty(),
                onQueryChange = {
                    viewModel.setEvent(UsersContract.Event.OnQueryChanged(it))
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    ) {

        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                is UsersContract.Effect.GoToChat -> {
                    val cid = (effect as UsersContract.Effect.GoToChat).cid
                    navController.navigate("${Screen.Channel.route}/$cid")
                }
                is UsersContract.Effect.SearchFailure -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as MainContract.Effect.ShowErrorSnackbar)
                                    .message.orEmpty()
                            )
                    }
                }
            }
        })

        if(state.loading) Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color.Blue)
        }
        else {
            LazyColumn() {
                state.users?.forEach { user ->
                    item {
                        UserItem(
                            user = user,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.setEvent(UsersContract.Event.OnUserClick(user.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun convertDate(milliseconds: Long): String {
    return DateFormat.format("dd/MM/yyyy hh:mm", milliseconds).toString()
}

@Composable
fun UserItem(
    user: User,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ChatTheme() {
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
                UserAvatar(user = user, modifier = Modifier.size(40.dp))
                Column() {
                    Text(text = user.id, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = convertDate(user.lastActive!!.time), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun UsersToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit
) {

    var searchExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        Row(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if(searchExpanded) searchExpanded = false
                    else onBackPressed()
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        "contentDescription",
                        tint = Color.Black
                    )
                }

                if(!searchExpanded) {
                    Text(
                        text = "Users",
                        fontSize = 18.sp
                    )
                }
            }

            if(searchExpanded) {
                ChatTheme() {
                    SearchInput(
                        modifier = Modifier.fillMaxWidth(),
                        query = query,
                        label = { Text("Search") },
                        onValueChange = {
                            onQueryChange(it)
                        }
                    )
                }
            } else {
                IconButton(onClick = {
                    searchExpanded = true
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_search_24),
                        "contentDescription",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UserPreview() {
    UserItem(User())
}
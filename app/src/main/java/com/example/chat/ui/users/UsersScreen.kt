package com.example.chat.ui.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chat.ui.models.Screen
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
                query = state.query,
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
                    navController.navigate(Screen.Channel.createRoute(cid))
                }
                is UsersContract.Effect.SearchFailure -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as UsersContract.Effect.SearchFailure)
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

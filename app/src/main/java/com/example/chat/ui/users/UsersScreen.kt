package com.example.chat.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.chat.ui.models.Screen
import com.example.data.mappers.toDataModel
import io.getstream.chat.android.compose.ui.common.LoadingView
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

        val lazyUsers = state.usersFlow.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.background(ChatTheme.colors.appBackground).fillMaxSize()
        ) {
            items(lazyUsers) { user ->
                UserItem(
                    user = user!!.toDataModel(),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setEvent(UsersContract.Event.OnUserClick(user.id))
                    }
                )
            }

            lazyUsers.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            LoadingView(
                                modifier = Modifier.fillMaxWidth().padding(8.dp)
                            )
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = lazyUsers.loadState.refresh as LoadState.Error
                        item {
                            Text(
                                text = e.error.message.orEmpty(),
                                modifier = Modifier.fillParentMaxSize().clickable { retry() }
                            )
                            /*ErrorItem(
                                message = e.error.localizedMessage!!,
                                modifier = Modifier.fillParentMaxSize(),
                                onClickRetry = { retry() }
                            )*/
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = lazyUsers.loadState.append as LoadState.Error
                        item {
                            Text(
                                text = e.error.message.orEmpty(),
                                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { retry() }
                            )
                            /*ErrorItem(
                                message = e.error.localizedMessage!!,
                                onClickRetry = { retry() }
                            )*/
                        }
                    }
                }
            }
        }
    }
}

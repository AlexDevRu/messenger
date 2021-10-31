package com.example.chat.ui.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import com.example.chat.ui.chat.ChannelScreen
import com.example.chat.ui.edit_profile.EditProfileScreen
import com.example.chat.ui.models.DrawerMenuItem
import com.example.chat.ui.models.Screen
import com.example.chat.ui.settings.SettingsScreen
import com.example.chat.ui.users.UsersScreen
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.channel.list.ChannelsState
import io.getstream.chat.android.compose.ui.channel.header.ChannelListHeader
import io.getstream.chat.android.compose.ui.channel.list.ChannelList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channel.ChannelViewModelFactory
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
private fun Preview() {
    Drawer(onDestinationClick = {}, activeRoute = "")
}

private val drawerScreens = listOf(
    DrawerMenuItem.Channels,
    DrawerMenuItem.Settings,
    DrawerMenuItem.Logout
)

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClick: (route: String) -> Unit,
    onEditProfileClick: () -> Unit = {},
    activeRoute: String,
    currentUser: User? = null
) {
    val constraintSet = ConstraintSet {
        val userAvatarRef = createRefFor("userAvatar")
        val editProfileButtonRef = createRefFor("editProfileButton")
        val menuItemsRef = createRefFor("menuItems")

        constrain(userAvatarRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(editProfileButtonRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end, 16.dp)
        }

        constrain(menuItemsRef) {
            top.linkTo(userAvatarRef.bottom)
        }
    }

    ConstraintLayout(
        constraintSet,
        modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        UserAvatar(
            modifier = Modifier.layoutId("userAvatar"),
            currentUser = currentUser
        )

        IconButton(onClick = onEditProfileClick, modifier = Modifier.layoutId("editProfileButton")) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_edit_24),
                "contentDescription",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .layoutId("menuItems")
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        ) {
            drawerScreens.forEach { screen ->
                NavMenuItem(
                    screen = screen,
                    onDestinationClicked = onDestinationClick,
                    active = screen.route == activeRoute
                )
            }
        }
    }
}

@Composable
private fun UserAvatar(
    modifier: Modifier = Modifier,
    currentUser: User? = null
) {
    ChatTheme() {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            io.getstream.chat.android.compose.ui.common.avatar.UserAvatar(
                user = currentUser ?: User(),
                modifier = modifier
                    .size(100.dp, 100.dp),
                contentDescription = stringResource(id = R.string.app_name)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = currentUser?.id.orEmpty(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = currentUser?.name.orEmpty(), fontSize = 20.sp)
        }
    }
}


@Composable
private fun NavMenuItem(
    screen: DrawerMenuItem,
    onDestinationClicked: (route: String) -> Unit,
    active: Boolean = false
) {
    val color = if(active) Color.Blue else Color.Black

    Box(modifier = Modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        ) {
            onDestinationClicked(screen.route)
        }
        .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp, 16.dp)
        ) {
            Icon(
                painterResource(id = screen.iconRes),
                "contentDescription",
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(id = screen.displayTitle),
                fontSize = 16.sp,
                color = color,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun MainScreen(
    navigateToAuth: () -> Unit,
    viewModel: MainVM = getViewModel()
) {

    val navController = rememberNavController()

    var activeRoute by remember {
        mutableStateOf(navController.currentDestination?.route)
    }

    val mainState by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) {

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val openDrawer = {
            scope.launch { drawerState.open() }
        }
        val closeDrawer = {
            scope.launch { drawerState.close() }
        }

        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                MainContract.Effect.Logout -> {
                    navigateToAuth()
                }
                is MainContract.Effect.ShowErrorSnackbar -> {
                    Log.e("asd", (effect as MainContract.Effect.ShowErrorSnackbar).message.orEmpty())
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

        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                Drawer(
                    onDestinationClick = { route ->
                        closeDrawer()
                        navController.navigate(route)
                    },
                    onEditProfileClick = {
                        closeDrawer()
                        navController.navigate(Screen.EditProfile.route)
                    },
                    currentUser = mainState.user,
                    activeRoute = activeRoute.orEmpty()
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = DrawerMenuItem.Channels.route
            ) {
                composable(DrawerMenuItem.Channels.route) {
                    ChannelsScreen(
                        navController = navController,
                        openDrawer = { openDrawer() },
                        currentUser = mainState.user,
                        loading = mainState.loading
                    )
                    activeRoute = DrawerMenuItem.Channels.route
                }
                composable(DrawerMenuItem.Settings.route) {
                    SettingsScreen()
                    activeRoute = DrawerMenuItem.Settings.route
                }
                composable(Screen.Channel.route) {
                    val channelId = it.arguments?.getString("channelId")
                    ChannelScreen(channelId!!, onBackPressed = { navController.navigateUp() })
                }
                composable(Screen.Users.route) {
                    UsersScreen(navController)
                }
                composable(Screen.EditProfile.route) {
                    EditProfileScreen(
                        onCancel = { navController.navigateUp() },
                        onSuccess = {
                            viewModel.setEvent(MainContract.Event.OnUserUpdated(it))
                        }
                    )
                }
                dialog(DrawerMenuItem.Logout.route) {
                    Surface(color = MaterialTheme.colors.background) {
                        AlertDialog(
                            onDismissRequest = { navController.navigateUp() },
                            title = {
                                Text(stringResource(id = R.string.logout_question))
                            },
                            text = {
                                Text(stringResource(id = R.string.logout_confirm))
                            },
                            confirmButton = {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setEvent(MainContract.Event.OnLogout)
                                    }
                                ) {
                                    Text(stringResource(id = R.string.logout))
                                }
                            },
                            dismissButton  = {
                                OutlinedButton(
                                    onClick = { navController.navigateUp() }
                                ) {
                                    Text(stringResource(id = R.string.dismiss))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

//@Preview
@Composable
fun ChannelsScreen(
    navController: NavController,
    openDrawer: () -> Unit = {},
    currentUser: User? = null,
    loading: Boolean = true
) {
    ChatTheme() {
        Column {
            ChannelListHeader(modifier = Modifier.fillMaxWidth(),
                currentUser = currentUser,
                leadingContent = {
                    IconButton(onClick = {
                       openDrawer()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_menu_24),
                            "contentDescription",
                            tint = Color.Black
                        )
                    }
                },
                title = stringResource(R.string.app_name),
                trailingContent = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Users.route)
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_search_24),
                            "contentDescription",
                            tint = Color.Black
                        )
                    }
                }
            )

            if(!loading && currentUser != null) {
                ChannelList(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel(
                        factory = ChannelViewModelFactory(
                            ChatClient.instance(),
                            ChatDomain.instance(),
                            QuerySort.desc("last_updated"),
                            Filters.and(
                                Filters.eq("type", "messaging"),
                                Filters.`in`("members", listOf(currentUser.id))
                            )
                        )
                    ),
                    onChannelClick = {
                        navController.navigate("${Screen.Channel.route}/${it.cid}")
                    },
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color.Blue)
                }
            }
        }
    }
}

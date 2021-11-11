package com.example.chat.ui.main

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomAlertDialog
import com.example.chat.ui.channels.ChannelsScreen
import com.example.chat.ui.chat.ChannelScreen
import com.example.chat.ui.contacts.ContactsScreen
import com.example.chat.ui.edit_profile.EditProfileScreen
import com.example.chat.ui.main.drawer.Drawer
import com.example.chat.ui.models.*
import com.example.chat.ui.phone.PhoneScreen
import com.example.chat.ui.settings.SettingsScreen
import com.example.chat.ui.settings.SettingsVM
import com.example.chat.ui.users.UsersScreen
import com.example.chat.user_info.UserInfoScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    navigateToAuth: () -> Unit,
    settingsVM: SettingsVM,
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

        LaunchedEffect(key1 = effect) {
            when(effect) {
                MainContract.Effect.Logout -> {
                    navigateToAuth()
                }
                is MainContract.Effect.ShowErrorSnackbar -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as MainContract.Effect.ShowErrorSnackbar)
                                    .message.orEmpty()
                            )
                    }
                }
            }
        }

        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                Drawer(
                    onDestinationClick = { route ->
                        closeDrawer()
                        //navController.navigate(route)
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    onEditProfileClick = {
                        closeDrawer()
                        navController.navigate(Screen.EditProfile.route)
                    },
                    userIsLoading = mainState.loading,
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
                        loading = mainState.loading
                    )
                    activeRoute = DrawerMenuItem.Channels.route
                }
                composable(DrawerMenuItem.Phone.route) {
                    PhoneScreen(onSkip = { navController.navigateUp() }, onSuccess = {
                        navController.navigateUp()
                    }, cancelLabel = R.string.cancel)
                    activeRoute = DrawerMenuItem.Phone.route
                }
                composable(DrawerMenuItem.Contacts.route) {
                    ContactsScreen(
                        onBackPressed = { navController.navigateUp() },
                        goToChannel = { navController.navigate(Screen.Channel.createRoute(it)) }
                    )
                    activeRoute = DrawerMenuItem.Contacts.route
                }
                composable(DrawerMenuItem.Settings.route) {
                    SettingsScreen(onBackPressed = { navController.navigateUp() }, settingsVM)
                    activeRoute = DrawerMenuItem.Settings.route
                }
                composable(Screen.Channel.route) {
                    val channelId = it.arguments?.getString(channelCidArgName)
                    ChannelScreen(
                        cid = channelId!!,
                        onBackPressed = { navController.navigateUp() },
                        onChannelAvatarClick = {
                            navController.navigate(Screen.UserInfoScreen.createRoute(it))
                        }
                    )
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
                composable(Screen.UserInfoScreen.route, arguments = listOf(
                    navArgument(userArgName) {
                        type = UserParamType()
                    }
                )) {
                    val userArg = it.arguments!!.getParcelable<ChatUserArg>(userArgName)!!
                    UserInfoScreen(user = userArg.toDomainModel(), onBackPressed = { navController.navigateUp() })
                }
                dialog(DrawerMenuItem.Logout.route) {
                    CustomAlertDialog(
                        title = R.string.logout_question,
                        text = R.string.logout_confirm,
                        confirmButtonText = R.string.logout,
                        dismissButtonText = R.string.dismiss,
                        onConfirm = { viewModel.setEvent(MainContract.Event.OnLogout) },
                        onDismiss = { navController.navigateUp() }
                    )
                }
                dialog(Screen.DeleteChannel.route) {

                    val cid = it.arguments?.getString(channelCidArgName)!!

                    CustomAlertDialog(
                        title = R.string.delete_channel_dialog_title,
                        text = R.string.delete_channel_dialog_text,
                        confirmButtonText = R.string.delete_channel_confirm,
                        dismissButtonText = R.string.dismiss,
                        onConfirm = {
                            viewModel.deleteChannel(cid)
                            navController.navigateUp()
                        },
                        onDismiss = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

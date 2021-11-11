package com.example.chat.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomIconButton
import com.example.chat.ui.models.Screen
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.compose.ui.channel.header.ChannelListHeader
import io.getstream.chat.android.compose.ui.channel.list.ChannelList
import io.getstream.chat.android.compose.ui.common.LoadingView
import io.getstream.chat.android.compose.viewmodel.channel.ChannelViewModelFactory
import io.getstream.chat.android.offline.ChatDomain

//@Preview
@Composable
fun ChannelsScreen(
    navController: NavController,
    openDrawer: () -> Unit = {},
    loading: Boolean = true
) {

    val currentUser by ChatDomain.instance().user.collectAsState()

    val isOnline by ChatDomain.instance().online.collectAsState()

    Column {
        ChannelListHeader(modifier = Modifier.fillMaxWidth(),
            currentUser = currentUser,
            isNetworkAvailable = isOnline,
            leadingContent = {
                CustomIconButton(
                    iconRes = R.drawable.ic_baseline_menu_24,
                    onClick = openDrawer
                )
            },
            title = stringResource(R.string.app_name),
            trailingContent = {
                CustomIconButton(
                    iconRes = R.drawable.ic_baseline_person_search_24,
                    onClick = { navController.navigate(Screen.Users.route) }
                )
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
                            Filters.`in`("members", listOf(currentUser!!.id))
                        )
                    )
                ),
                onChannelClick = {
                    navController.navigate(Screen.Channel.createRoute(it.cid))
                },
                onChannelLongClick = {
                    navController.navigate(Screen.DeleteChannel.createRoute(it.cid))
                }
            )
        } else {
            LoadingView(modifier = Modifier.fillMaxSize())
        }
    }
}

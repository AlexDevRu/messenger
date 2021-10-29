package com.example.chat.ui.users

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
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
import androidx.navigation.NavController
import com.example.chat.R
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.SearchInput
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun UsersScreen(navController: NavController, viewModel: UsersVM = getViewModel()) {

    val state by viewModel.uiState.collectAsState()

    var searchExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            
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
                            else navController.navigateUp()
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    if(searchExpanded) {
                        ChatTheme() {
                            SearchInput(
                                modifier = Modifier.fillMaxWidth(),
                                query = state.query.orEmpty(),
                                label = { Text("Search") },
                                onValueChange = {
                                    viewModel.setEvent(UsersContract.Event.OnQueryChanged(it))
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
    ) {
        if(state.loading) Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn() {
                state.users?.forEach { user ->
                    item {
                        UserItem(
                            user = user,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {

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

@Preview
@Composable
fun UserPreview() {
    UserItem(User())
}
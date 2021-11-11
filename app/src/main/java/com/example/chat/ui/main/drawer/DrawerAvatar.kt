package com.example.chat.ui.main.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.extensions.email
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.offline.ChatDomain

@Composable
fun DrawerAvatar(
    modifier: Modifier = Modifier,
    loading: Boolean
) {

    val user by ChatDomain.instance().user.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(loading) {
            Box(
                modifier = modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(70.dp, 70.dp))
            }
        } else {
            UserAvatar(
                modifier = Modifier.size(100.dp),
                user = user ?: User()
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = user?.name.orEmpty(), fontSize = 20.sp)
            Text(text = user?.email.orEmpty(), fontSize = 14.sp)
        }
    }
}
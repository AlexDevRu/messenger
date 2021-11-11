package com.example.chat.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.utils.toReadableString
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar

@Composable
fun UserItem(
    user: User,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
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
            UserAvatar(user = user, modifier = Modifier.size(40.dp))
            Column {
                Text(
                    text = if(user.name.isEmpty()) user.id else user.name,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.lastActive.toReadableString(),
                    fontSize = 12.sp
                )
            }
        }
    }
}
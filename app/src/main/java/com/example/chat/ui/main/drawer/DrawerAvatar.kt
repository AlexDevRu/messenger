package com.example.chat.ui.main.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.R
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar

@Composable
fun DrawerAvatar(
    modifier: Modifier = Modifier,
    currentUser: User? = null
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        UserAvatar(
            user = currentUser ?: User(),
            modifier = modifier.size(100.dp, 100.dp),
            contentDescription = stringResource(id = R.string.app_name)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = currentUser?.id.orEmpty(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = currentUser?.name.orEmpty(), fontSize = 20.sp)
    }
}
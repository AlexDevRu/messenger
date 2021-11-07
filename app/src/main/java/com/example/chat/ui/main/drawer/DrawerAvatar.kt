package com.example.chat.ui.main.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.example.chat.utils.globalVM
import com.example.data.mappers.toDataModel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar

@Composable
fun DrawerAvatar(
    modifier: Modifier = Modifier,
    loading: Boolean
) {

    val globalVM = LocalContext.current.globalVM()
    val user by globalVM.user.collectAsState()

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .componentRegistry {
            add (SvgDecoder( LocalContext.current) )
        }
        .build()

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
                user = user?.toDataModel() ?: User()
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = user?.userName.orEmpty(), fontSize = 20.sp)
            Text(text = user?.email.orEmpty(), fontSize = 14.sp)
        }
    }
}
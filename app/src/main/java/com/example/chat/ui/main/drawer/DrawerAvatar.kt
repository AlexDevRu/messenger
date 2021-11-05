package com.example.chat.ui.main.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import com.example.chat.R
import com.example.data.models.getAvatarOrDefault
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar

@Composable
fun DrawerAvatar(
    modifier: Modifier = Modifier,
    loading: Boolean,
    currentUser: ChatUser? = null
) {

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
            /*UserAvatar(
                user = currentUser ?: User(),
                modifier = Modifier.size(100.dp, 100.dp),
                contentDescription = stringResource(id = R.string.app_name)
            )*/
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                Image(
                    painter = rememberImagePainter(currentUser?.getAvatarOrDefault() ?: ""),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = currentUser?.userName.orEmpty(), fontSize = 20.sp)
            Text(text = currentUser?.email.orEmpty(), fontSize = 14.sp)
        }
    }
}
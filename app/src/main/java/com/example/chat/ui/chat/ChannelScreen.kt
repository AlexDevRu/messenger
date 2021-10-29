package com.example.chat.ui.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Preview
@Composable
private fun Preview() {
    ChannelScreen(channelId = "cid")
}

@Composable
fun ChannelScreen(channelId: String) {
    ChatTheme {
        //Text(text = channelId)
        MessagesScreen(channelId = channelId)
    }
}
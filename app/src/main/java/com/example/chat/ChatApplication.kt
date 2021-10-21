package com.example.chat

import android.app.Application
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.livedata.ChatDomain

class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val client = ChatClient.Builder(getString(R.string.api_key), this)
            .logLevel(ChatLogLevel.ALL)
            .build()

        // Step 2 - Set up the domain for offline storage
        ChatDomain.Builder(client, this)
            // Enable offline support
            .offlineEnabled()
            .build()
    }
}
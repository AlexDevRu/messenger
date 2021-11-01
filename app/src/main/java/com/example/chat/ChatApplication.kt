package com.example.chat

import android.app.Application
import com.example.chat.di.dataModule
import com.example.chat.di.useCaseModule
import com.example.chat.di.viewModelModule
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.offline.ChatDomain
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val notificationConfig = NotificationConfig(
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator())
        )

        val client = ChatClient.Builder(getString(R.string.api_key), this)
            .logLevel(ChatLogLevel.ALL)
            .notifications(notificationConfig)
            .build()

        // Step 2 - Set up the domain for offline storage
        ChatDomain.Builder(client, this)
            // Enable offline support
            .offlineEnabled()
            .build()

        startKoin {
            androidContext(this@ChatApplication)
            modules(dataModule, useCaseModule, viewModelModule)
        }
    }
}
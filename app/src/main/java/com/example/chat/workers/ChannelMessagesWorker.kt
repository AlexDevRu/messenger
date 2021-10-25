package com.example.chat.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.subscribeFor

/*class ChannelMessagesWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext

        val disposable = ChatClient.instance().subscribeFor<NewMessageEvent> { newMessageEvent ->
            val message = newMessageEvent.message
        }


    }
}*/
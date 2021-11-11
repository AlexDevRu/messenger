package com.example.chat.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.chat.R
import com.example.chat.ui.MainActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.PushMessage
import io.getstream.chat.android.client.notifications.handler.NotificationHandler

class MyNotificationHandler(private val context: Context) : NotificationHandler {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val channelId: String by lazy {
        "my_channel"//context.resources.getString(R.string.stream_chat_notification_channel_id)
    }

    override fun showNotification(channel: Channel, message: Message) {
        Log.e("asd", "sjdhfkjhskfhsdkjf")
        pushNotification(message)
    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        // Dismiss all notification related with this channel
    }

    override fun dismissAllNotifications() {
        // Dismiss all notifications
    }

    /*override fun onChatEvent(event: NewMessageEvent): Boolean {
        val message = event.message
        pushNotification(message)
        return true
    }*/

    private fun pushNotification(message: Message) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(message.user.name)
            setContentText(message.text)
            setSmallIcon(R.mipmap.ic_launcher)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    /*override fun onPushMessage(message: PushMessage): Boolean {
        ChatClient.instance().getMessage(message.messageId).enqueue {
            if(it.isSuccess) {
                pushNotification(it.data())
            }
        }
        return true
    }*/
}
package com.example.chat.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.users.UsersContract
import com.example.chat.ui.users.UsersVM
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatVM: ViewModel() {

    companion object {
        private const val TAG = "ChatVM"
    }

    private val client = ChatClient.instance()

    fun deleteChannel(channelId: String) {
        Log.w(TAG, "deleteChannel")

        /*client.queryChannels(
            QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq("cid", channelId),
                    Filters.`in`("members", listOf(client.getCurrentUser()!!.id))
                ),
                offset = 0,
                limit = 1,
                messageLimit = 1
            )
        ).enqueue { result ->
            if (result.isSuccess) {
                val channels: List<Channel> = result.data()
                Log.w(TAG, "channels ${channels}")
                Log.w(TAG, "channels messages ${channels.firstOrNull()?.messages}")
                if(channels.firstOrNull()?.messages.isNullOrEmpty()) {
                    Log.w(TAG, "delete channel")
                    ChatDomain.instance().deleteChannel(channelId).enqueue {
                        if(result.isSuccess) {
                            Log.w(TAG, "channel deleted")
                        } else {
                            Log.w(TAG, "channel delete error ${result.error().message}")
                        }
                    }
                }
            } else {
                Log.w(TAG, "result error ${result.error().message}")
            }
        }*/

        ChatDomain.instance().deleteChannel(channelId).enqueue { result ->
            if(result.isSuccess) {
                Log.w(TAG, "channel deleted")
            } else {
                Log.w(TAG, "channel delete error ${result.error().message}")
            }
        }
    }

    private fun createNewChannel(selectedUser: String) {
        client.createChannel(
            channelType = "messaging",
            members = listOf(client.getCurrentUser()!!.id, selectedUser)
        ).enqueue { result ->
            if (result.isSuccess) {

            } else {
                Log.e(TAG, result.error().message.toString())
            }
        }
    }

    /*fun createChannelByCid(cid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = client.queryChannels(
                QueryChannelsRequest(
                    filter = Filters.and(
                        Filters.eq("cid", cid),
                        Filters.`in`("members", listOf(client.getCurrentUser()!!.id))
                    ),
                    offset = 0,
                    limit = 1,
                    messageLimit = 1
                )
            ).execute()

            result.data().firstOrNull()
        }
    }*/
}
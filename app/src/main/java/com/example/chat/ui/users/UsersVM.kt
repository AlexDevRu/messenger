package com.example.chat.ui.users

import android.util.Log
import com.example.chat.ui.base.BaseViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters

class UsersVM: BaseViewModel<UsersContract.Event, UsersContract.State, UsersContract.Effect>() {

    companion object {
        private const val TAG = "UsersVM"
    }

    private val client = ChatClient.instance()

    init {
        queryAllUsers()
    }

    override fun createInitialState(): UsersContract.State {
        return UsersContract.State(
            users = null,
            query = null,
            loading = false
        )
    }

    override fun handleEvent(event: UsersContract.Event) {
        when(event) {
            is UsersContract.Event.OnQueryChanged -> {
                setState { copy(query = event.query) }
                if(event.query.isNullOrEmpty()) queryAllUsers() else searchUser(event.query)
            }
            is UsersContract.Event.OnUserClick -> createNewChannel(event.userId)
        }
    }

    private fun queryAllUsers() {
        val request = QueryUsersRequest(
            filter = Filters.ne("id", client.getCurrentUser()!!.id),
            offset = 0,
            limit = 100
        )
        getUsersByRequest(request)
    }

    private fun searchUser(query: String) {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
            filter = filters,
            offset = 0,
            limit = 100
        )
        getUsersByRequest(request)
    }

    private fun getUsersByRequest(request: QueryUsersRequest) {
        setState { copy(loading = true) }
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users = result.data()
                setState { copy(users = users) }
            } else {
                Log.e(TAG, result.error().message.toString())
                setEffect { UsersContract.Effect.SearchFailure(result.error().message) }
            }
            setState { copy(loading = false) }
        }
    }

    private fun createNewChannel(selectedUser: String) {
        client.createChannel(
            channelType = "messaging",
            members = listOf(client.getCurrentUser()!!.id, selectedUser)
        ).enqueue { result ->
            if (result.isSuccess) {
                setEffect { UsersContract.Effect.GoToChat(result.data().cid) }
            } else {
                Log.e(TAG, result.error().message.toString())
            }
        }
    }
}
package com.example.chat.ui.contacts

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.CreateChannelUseCase
import com.example.domain.use_cases.remote.GetUsersByPhoneNumbersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsVM(
    private val app: Application,
    private val getUsersByPhoneNumbersUseCase: GetUsersByPhoneNumbersUseCase,
    private val createChannelUseCase: CreateChannelUseCase
): BaseViewModel<ContactsContract.Event, ContactsContract.State, ContactsContract.Effect>() {

    companion object {
        private const val TAG = "ContactsVM"

        private const val CONTACT_ID = android.provider.ContactsContract.Contacts._ID
        private const val DISPLAY_NAME = android.provider.ContactsContract.Contacts.DISPLAY_NAME
        private const val HAS_PHONE_NUMBER = android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER
        private const val PHONE_NUMBER = android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
        private const val PHONE_CONTACT_ID = android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    }

    override fun createInitialState(): ContactsContract.State {
        return ContactsContract.State(
            contacts = emptyList(),
            users = emptyList()
        )
    }

    override fun handleEvent(event: ContactsContract.Event) {
        when(event) {
            ContactsContract.Event.OnReadContacts -> viewModelScope.launch(Dispatchers.IO) { readContacts() }
            is ContactsContract.Event.OnUserClick -> createNewChannel(event.userId)
        }
    }

    @SuppressLint("Range")
    suspend fun readContacts() {

        setState { copy(loading = true) }

        val contacts = mutableListOf<Contact>()

        val phones = mutableMapOf<Int, List<String>>()

        val cr = app.contentResolver
        val pCur = cr.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(PHONE_NUMBER, PHONE_CONTACT_ID),
            null,
            null,
            null
        )
        if (pCur != null && pCur.count > 0) {
            while (pCur.moveToNext()) {
                val contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID))
                var curPhones = mutableListOf<String>()
                if (phones.containsKey(contactId)) {
                    curPhones = phones[contactId]!!.toMutableList()
                }
                curPhones.add(pCur.getString(pCur.getColumnIndex(PHONE_NUMBER)))
                phones[contactId] = curPhones
            }
            val cur = cr.query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                arrayOf(CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER),
                "$HAS_PHONE_NUMBER > 0",
                null,
                "$DISPLAY_NAME ASC"
            )
            if (cur != null) {
                if (cur.count > 0) {
                    while (cur.moveToNext()) {
                        val id = cur.getInt(cur.getColumnIndex(CONTACT_ID))
                        if (phones.containsKey(id)) {
                            val con = Contact(
                                contactName = cur.getString(cur.getColumnIndex(DISPLAY_NAME)),
                                phoneNumbers = phones[id]!!
                            )
                            contacts.add(con)
                        }
                    }
                }
                cur.close()
            }
        }

        pCur?.close()

        val phoneNumbers = mutableListOf<String>()
        for(pList in phones.values)
            for(p in pList)
                phoneNumbers.add(p.replace(Regex("\\s|-"), ""))

        Log.e(TAG, "phoneNumbers $phoneNumbers")

        val result = getUsersByPhoneNumbersUseCase(phoneNumbers)

        when(result) {
            is Result.Success -> {
                withContext(Dispatchers.Default) {

                    Log.d(TAG, "users in result ${result.value}")

                    for(user in result.value) {
                        val contact = contacts.find {
                            Log.e("asd", "${it.phoneNumbers}")
                            it.phoneNumbers.map { it.replace(Regex("\\s|-"), "") }.contains(user.phone)
                        }
                        Log.e(TAG, "user $user")
                        Log.e(TAG, "contact $contact")
                        Log.e(TAG, "===============================")
                        contact?.user = user
                    }
                }
            }
            is Result.Failure -> {
                Log.e(TAG, "error contacts ${result.throwable.message}")
            }
        }

        setState { copy(contacts = contacts, loading = false) }
    }

    private fun createNewChannel(selectedUserId: String) {
        viewModelScope.launch {
            val result = createChannelUseCase(selectedUserId)
            when(result) {
                is Result.Success -> setEffect { ContactsContract.Effect.GoToChat(result.value) }
                is Result.Failure -> {}
            }
        }
    }
}
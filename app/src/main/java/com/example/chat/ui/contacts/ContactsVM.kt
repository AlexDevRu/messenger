package com.example.chat.ui.contacts

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsVM(
    private val app: Application
): BaseViewModel<ContactsContract.Event, ContactsContract.State, ContactsContract.Effect>() {

    companion object {
        private const val CONTACT_ID = android.provider.ContactsContract.Contacts._ID
        private const val DISPLAY_NAME = android.provider.ContactsContract.Contacts.DISPLAY_NAME
        private const val HAS_PHONE_NUMBER = android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER
        private const val PHONE_NUMBER = android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
        private const val PHONE_CONTACT_ID = android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    }

    override fun createInitialState(): ContactsContract.State {
        return ContactsContract.State(
            contacts = emptyList()
        )
    }

    override fun handleEvent(event: ContactsContract.Event) {
        when(event) {
            ContactsContract.Event.OnReadContacts -> viewModelScope.launch(Dispatchers.IO) { readContacts() }
        }
    }

    @SuppressLint("Range")
    fun readContacts() {

        setState { copy(loading = true) }

        val contacts = mutableListOf<Contact>()

        val cr = app.contentResolver
        val pCur = cr.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(PHONE_NUMBER, PHONE_CONTACT_ID),
            null,
            null,
            null
        )
        if (pCur != null && pCur.count > 0) {
            val phones = mutableMapOf<Int, List<String>>()
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
                                name = cur.getString(cur.getColumnIndex(DISPLAY_NAME)),
                                phoneNumber = phones[id]!!.joinToString(", ")
                            )
                            contacts.add(con)
                        }
                    }
                }
                cur.close()
            }
        }

        pCur?.close()

        setState { copy(contacts = contacts, loading = false) }
    }
}
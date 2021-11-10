package com.example.chat.user_info

import android.app.Application
import android.content.ContentProviderOperation
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.GetPhoneByUserIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserInfoVM(
    private val app: Application,
    private val getPhoneByUserIdUseCase: GetPhoneByUserIdUseCase
): BaseViewModel<UserInfoContract.Event, UserInfoContract.State, UserInfoContract.Effect>() {

    companion object {
        private const val TAG = "UserInfoVM"
    }

    override fun createInitialState(): UserInfoContract.State {
        return UserInfoContract.State(
            phone = null,
            loading = false,
            isContactInsertInProgress = false
        )
    }

    override fun handleEvent(event: UserInfoContract.Event) {
        when(event) {
            is UserInfoContract.Event.OnGetUserPhoneById -> getUserPhoneById(event.uid)
            is UserInfoContract.Event.OnAddContact -> addContact(event.userName)
        }
    }

    private fun getUserPhoneById(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loading = true) }

            val result = getPhoneByUserIdUseCase(uid)
            when(result) {
                is Result.Success -> setState { copy(phone = result.value) }
                is Result.Failure -> setEffect { UserInfoContract.Effect.ShowErrorMessage(result.throwable.message.orEmpty()) }
            }

            setState { copy(loading = false) }
        }
    }

    private fun addContact(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {

            setState { copy(isContactInsertInProgress = true) }

            val op = ArrayList<ContentProviderOperation>()

            /* Добавляем пустой контакт */
            op.add(
                ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            /* Добавляем данные имени */
            op.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, userName)
                    .build()
            )

            /* Добавляем данные телефона */
            op.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, currentState.phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )

            try {
                app.contentResolver.applyBatch(ContactsContract.AUTHORITY, op)
            } catch (e: Exception) {
                Log.e(TAG, e.message.orEmpty())
            }

            setState { copy(isContactInsertInProgress = false) }
        }
    }
}
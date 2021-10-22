package com.example.data.repositories.local

import android.content.Context
import com.example.domain.repositories.local.IPreferencesRepository

class PreferencesRepository(context: Context): IPreferencesRepository {

    companion object {
        private const val STORAGE_NAME = "Storage"

        private const val USER_ID = "USER_ID"
        private val USER_ID_DEFAULT = null
    }

    private val storage = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
    private val editor = storage.edit()

    override fun saveUser(userId: String?) {
        editor.putString(USER_ID, userId).apply()
    }

    override fun getUser() = storage.getString(USER_ID, USER_ID_DEFAULT)
}
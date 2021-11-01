package com.example.data.repositories.local

import android.content.Context
import com.example.domain.repositories.local.IPreferencesRepository

class PreferencesRepository(context: Context): IPreferencesRepository {

    companion object {
        private const val STORAGE_NAME = "Storage"

        private const val USER_ID = "USER_ID"
        private val USER_ID_DEFAULT = null

        private const val THEME = "THEME"
        private const val THEME_DEFAULT = false

        private const val LANGUAGE = "LANGUAGE"
        private const val LANGUAGE_DEFAULT = "en"
    }

    private val storage = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
    private val editor = storage.edit()

    override fun saveUser(userId: String?) {
        editor.putString(USER_ID, userId).apply()
    }

    override fun getUser() = storage.getString(USER_ID, USER_ID_DEFAULT)


    override fun saveTheme(isDark: Boolean) {
        editor.putBoolean(THEME, isDark).apply()
    }

    override fun getTheme() = storage.getBoolean(THEME, THEME_DEFAULT)

    override fun saveLanguage(lang: String) {
        editor.putString(LANGUAGE, lang).commit()
    }

    override fun getLanguage() = storage.getString(LANGUAGE, LANGUAGE_DEFAULT).toString()
}
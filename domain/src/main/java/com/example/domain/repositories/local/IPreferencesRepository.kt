package com.example.domain.repositories.local

interface IPreferencesRepository {
    fun saveUser(userId: String?)
    fun getUser(): String?

    fun saveTheme(isDark: Boolean)
    fun getTheme(): Boolean

    fun saveLanguage(lang: String)
    fun getLanguage(): String
}
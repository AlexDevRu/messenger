package com.example.domain.repositories.local

interface IPreferencesRepository {
    fun saveUser(userId: String?)
    fun getUser(): String?
}
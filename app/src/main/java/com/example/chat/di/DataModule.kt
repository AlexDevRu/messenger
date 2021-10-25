package com.example.chat.di

import com.example.data.repositories.local.PreferencesRepository
import com.example.data.repositories.remote.FirestoreRepository
import com.example.data.repositories.remote.StreamChatRepository
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<IPreferencesRepository> {
        PreferencesRepository(androidContext())
    }

    single<IFirestoreRepository> {
        FirestoreRepository()
    }

    single<IStreamChatRepository> {
        StreamChatRepository()
    }
}
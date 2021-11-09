package com.example.chat.di

import com.example.data.database.MessengerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        MessengerDatabase.getDatabase(androidContext())
    }
    single {
        get<MessengerDatabase>().userDao()
    }
    single {
        get<MessengerDatabase>().userRemoteKeysDao()
    }
}
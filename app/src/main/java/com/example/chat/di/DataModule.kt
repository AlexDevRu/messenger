package com.example.chat.di

import com.example.data.repositories.local.PreferencesRepository
import com.example.data.repositories.local.database.UsersRepository
import com.example.data.repositories.remote.FirebaseAuthRepository
import com.example.data.repositories.remote.FirebaseStorageRepository
import com.example.data.repositories.remote.FirestoreRepository
import com.example.data.repositories.remote.StreamChatRepository
import com.example.domain.repositories.local.IPreferencesRepository
import com.example.domain.repositories.local.database.IUsersRepository
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.repositories.remote.IStreamChatRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<IPreferencesRepository> {
        PreferencesRepository(androidContext())
    }

    single<IFirestoreRepository> {
        FirestoreRepository()
    }

    single<IFirebaseAuthRepository> {
        FirebaseAuthRepository()
    }

    single<IStreamChatRepository> {
        StreamChatRepository(get(), get())
    }

    single<IFirebaseStorageRepository> {
        FirebaseStorageRepository(androidApplication())
    }

    single<IUsersRepository> {
        UsersRepository(get())
    }
}
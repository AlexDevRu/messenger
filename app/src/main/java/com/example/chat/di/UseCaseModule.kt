package com.example.chat.di

import com.example.domain.use_cases.local.preferences.*
import com.example.domain.use_cases.remote.SignInUserUseCase
import com.example.domain.use_cases.remote.SignUpUserUseCase
import com.example.domain.use_cases.remote.DeleteChannelUseCase
import com.example.domain.use_cases.remote.GetUserByIdUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single {
        SignInUserUseCase(get(), get(), get())
    }
    single {
        SignUpUserUseCase(get(), get(), get())
    }
    single {
        GetUserByIdUseCase(get())
    }
    single {
        DeleteChannelUseCase(get())
    }

    single {
        GetUserUseCase(get())
    }
    single {
        SaveUserUseCase(get())
    }
    single {
        GetThemeUseCase(get())
    }
    single {
        SaveThemeUseCase(get())
    }
    single {
        GetLanguageUseCase(get())
    }
    single {
        SaveLanguageUseCase(get())
    }
}
package com.example.chat.di

import com.example.domain.use_cases.local.preferences.*
import com.example.domain.use_cases.remote.*
import org.koin.dsl.module

val useCaseModule = module {
    single {
        SignInUserUseCase(get(), get(), get())
    }
    single {
        SignUpUserUseCase(get(), get(), get())
    }
    single {
        GetUserByIdUseCase(get(), get())
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

    single {
        UpdateUserUseCase(get(), get())
    }

    single {
        LogoutUseCase(get(), get(), get())
    }

    single {
        SavePhoneUseCase(get())
    }

    single {
        LinkPhoneToAccountUseCase(get())
    }
}
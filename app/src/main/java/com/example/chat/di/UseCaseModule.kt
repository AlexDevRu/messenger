package com.example.chat.di

import com.example.domain.use_cases.local.preferences.*
import com.example.domain.use_cases.remote.*
import org.koin.dsl.module

val useCaseModule = module {
    single {
        SignInUserUseCase(get(), get(), get(), get())
    }
    single {
        SignUpUserUseCase(get(), get(), get(), get())
    }
    single {
        GetUserByIdUseCase(get(), get(), get())
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
        UpdateUserUseCase(get(), get(), get())
    }

    single {
        LogoutUseCase(get(), get(), get())
    }

    single {
        LinkPhoneToAccountUseCase(get(), get())
    }

    single {
        GetUsersByQueryUseCase(get())
    }

    single {
        GetUsersByPhoneNumbersUseCase(get(), get(), get())
    }

    single {
        CreateChannelUseCase(get())
    }
}
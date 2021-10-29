package com.example.chat.di

import com.example.chat.ui.edit_profile.EditProfileVM
import com.example.chat.ui.main.MainVM
import com.example.chat.ui.sign_in.SignInVM
import com.example.chat.ui.users.UsersVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SignInVM(get(), get())
    }
    viewModel {
        MainVM(get(), get())
    }
    viewModel {
        EditProfileVM()
    }
    viewModel {
        UsersVM()
    }
}




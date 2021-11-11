package com.example.chat.di

import com.example.chat.ui.auth.AuthVM
import com.example.chat.ui.auth.NewAvatarSignUpVM
import com.example.chat.ui.chat.ChatVM
import com.example.chat.ui.contacts.ContactsVM
import com.example.chat.ui.edit_profile.EditProfileVM
import com.example.chat.ui.main.MainVM
import com.example.chat.ui.phone.PhoneVM
import com.example.chat.ui.settings.SettingsVM
import com.example.chat.ui.users.UsersVM
import com.example.chat.user_info.UserInfoVM
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        AuthVM(get(), get())
    }
    viewModel {
        MainVM(get(), get(), get(), get())
    }
    viewModel {
        EditProfileVM(get())
    }
    viewModel {
        UsersVM(get(), get())
    }
    viewModel {
        ChatVM(get())
    }
    viewModel { params ->
        MessageListViewModel(params.get())
    }
    viewModel {
        SettingsVM(get(), get(), get(), get())
    }
    viewModel {
        ContactsVM(androidApplication(), get(), get())
    }
    viewModel {
        PhoneVM(get())
    }

    viewModel {
        NewAvatarSignUpVM(get())
    }

    viewModel {
        UserInfoVM(androidApplication(), get())
    }
}

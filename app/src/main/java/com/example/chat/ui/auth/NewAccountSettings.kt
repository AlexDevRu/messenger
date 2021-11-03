package com.example.chat.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.chat.ui.edit_profile.EditProfileScreen
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
private fun Preview() {
    NewAccountSettingsScreen({})
}


@Composable
fun NewAccountSettingsScreen(
    onSuccess: () -> Unit
) {
    EditProfileScreen(onSuccess = {
        onSuccess()
    }, hasToolbar = false)
}
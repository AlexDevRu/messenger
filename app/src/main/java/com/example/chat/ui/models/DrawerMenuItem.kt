package com.example.chat.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.chat.R

sealed class DrawerMenuItem(route: String, @StringRes val displayTitle: Int, @DrawableRes val iconRes: Int): Screen(route) {
    object Channels : DrawerMenuItem("Channels", R.string.channels, R.drawable.ic_channels)
    object Settings : DrawerMenuItem("Settings",
        R.string.settings,
        R.drawable.ic_baseline_settings_24
    )
    object Logout : DrawerMenuItem("Logout", R.string.logout, R.drawable.logout)
}
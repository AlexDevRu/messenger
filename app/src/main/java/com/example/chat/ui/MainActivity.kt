package com.example.chat.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.chat.ui.settings.SettingsVM
import com.example.chat.ui.theme.ComposeTheme
import com.example.domain.use_cases.local.preferences.GetLanguageUseCase
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity: AppCompatActivity() {

    private val getLanguageUseCase by inject<GetLanguageUseCase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setConfig()

        val settingsVM by viewModel<SettingsVM>()

        setContent {

            val state by settingsVM.uiState.collectAsState()

            ComposeTheme(darkTheme = state.isDark) {
                ChatTheme(isInDarkMode = state.isDark) {
                    Root(settingsVM)
                }
            }
        }
    }

    private fun setConfig() {
        val lang = getLanguageUseCase.invoke()

        Log.d("asd", "from shared preferences: lang $lang")

        val locale = Locale(lang)
        val newConfig = Configuration(resources.configuration)
        Locale.setDefault(locale)
        newConfig.setLocale(locale)
        baseContext.resources.updateConfiguration(newConfig, baseContext.resources.displayMetrics)
    }
}
package com.example.chat.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.Spinner
import com.example.chat.ui.base.composables.Toolbar
import com.example.chat.ui.models.DrawerMenuItem
import com.example.chat.utils.getActivity
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    settingsVM: SettingsVM = getViewModel()
) {

    val langList = listOf(
        "en",
        "ru"
    )

    val state by settingsVM.uiState.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Toolbar(DrawerMenuItem.Settings.displayText, onBackPressed)
        }
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ChatTheme.colors.appBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.dark_mode))
                    Switch(checked = state.isDark, onCheckedChange = {
                        settingsVM.setEvent(SettingsContract.Event.OnThemeChanged(it))
                    })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.language))
                    Spinner(onSelectedChanged = {
                        settingsVM.setEvent(SettingsContract.Event.OnLanguageChanged(it))
                        context.getActivity()?.recreate()
                    }, items = langList, text = state.lang)
                }
            }
        }
    }
}

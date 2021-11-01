package com.example.chat.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.Toolbar
import com.example.chat.ui.models.DrawerMenuItem
import com.example.chat.utils.getActivity
import org.koin.androidx.compose.getViewModel

@Preview
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


@Composable
fun DropDownList(
    requestToOpen: Boolean = false,
    list: List<String>,
    request: (Boolean) -> Unit,
    selectedString: (String) -> Unit
) {
    DropdownMenu(
        expanded = requestToOpen,
        onDismissRequest = { request(false) },
    ) {
        list.forEach {
            DropdownMenuItem(
                onClick = {
                    request(false)
                    selectedString(it)
                }
            ) {
                Text(it)
            }
        }
    }
}
@Composable
fun Spinner(
    onSelectedChanged: (String) -> Unit,
    items: List<String>,
    text: String
) {

    val isOpen = remember { mutableStateOf(false) }
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen.value = it
    }

    Box {
        Column {
            OutlinedTextField(
                value = text,
                readOnly = true,
                onValueChange = { onSelectedChanged(it) }
            )
            DropDownList(
                requestToOpen = isOpen.value,
                list = items,
                openCloseOfDropDownList,
                onSelectedChanged
            )
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { isOpen.value = true }
                )
        )
    }
}
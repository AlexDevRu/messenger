package com.example.chat.ui.phone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.chat.R
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.utils.getActivity

@Composable
fun CheckSmsCodeScreen(
    viewModel: PhoneVM
) {

    val context = LocalContext.current

    val state by viewModel.uiState.collectAsState()

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column() {
            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.sms_code,
                enabled = !state.applyChangedInProgress,
                textFieldVM = viewModel.smsCodeVM,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(PhoneContract.Event.OnSmsCodeValidate)
                },
                loading = state.applyChangedInProgress,
                textRes = R.string.confirm
            )
        }
    }
}
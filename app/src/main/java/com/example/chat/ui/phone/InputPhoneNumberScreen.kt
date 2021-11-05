package com.example.chat.ui.phone

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.utils.getActivity
import com.example.chat.utils.transformations.PhoneVisualTransformation

@Composable
fun InputPhoneNumber(
    viewModel: PhoneVM,
    @StringRes cancelLabel: Int,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    val state by viewModel.uiState.collectAsState()

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column() {
            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.phone,
                value = state.phone,
                leadingIcon = {
                    Text(
                        text = "+375-",
                        modifier = Modifier.padding(start = 16.dp, bottom = 3.dp),
                        color = LocalContentColor.current.copy(LocalContentAlpha.current)
                    )
                },
                onValueChanged = {
                    viewModel.setEvent(PhoneContract.Event.OnPhoneChanged(it))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                transformation = PhoneVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(PhoneContract.Event.OnPhoneNumberVerify(context.getActivity()!!))
                },
                loading = state.smsIsSending,
                textRes = R.string.get_sms_code
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCancel
            ) {
                Text(stringResource(cancelLabel), modifier = Modifier.padding(8.dp))
            }
        }
    }
}
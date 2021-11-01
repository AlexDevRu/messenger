package com.example.chat.ui.base.composables

import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun CustomAlertDialog(
    @StringRes title: Int,
    @StringRes text: Int,
    @StringRes confirmButtonText: Int,
    @StringRes dismissButtonText: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(color = MaterialTheme.colors.background) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(stringResource(title))
            },
            text = {
                Text(stringResource(text))
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(stringResource(confirmButtonText))
                }
            },
            dismissButton  = {
                OutlinedButton(
                    onClick = { onDismiss() }
                ) {
                    Text(stringResource(dismissButtonText))
                }
            }
        )
    }
}
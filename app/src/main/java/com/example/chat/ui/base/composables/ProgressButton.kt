package com.example.chat.ui.base.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chat.R

@Composable
fun ProgressButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = true,
    @StringRes textRes: Int
) {
    Button(
        onClick = {
            onClick()
        },
        enabled = !loading && enabled,
        modifier = modifier.background(colorResource(R.color.purple_700)),
    ) {
        if(loading) CircularProgressIndicator(modifier = Modifier.size(24.dp, 24.dp))
        Text(text = stringResource(textRes), modifier = Modifier.padding(8.dp), color = Color.White)
    }
}
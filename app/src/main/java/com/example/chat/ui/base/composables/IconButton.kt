package com.example.chat.ui.base.composables

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String = "",
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = {
        onClick()
    }) {
        Icon(
            painterResource(iconRes),
            contentDescription,
            tint = MaterialTheme.colors.onSurface
        )
    }
}
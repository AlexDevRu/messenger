package com.example.chat.ui.main.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.ui.models.DrawerMenuItem

@Composable
fun NavDrawerItem(
    screen: DrawerMenuItem,
    onDestinationClicked: (route: String) -> Unit,
    active: Boolean = false
) {
    val color = if(active) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

    Box(modifier = Modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        ) {
            onDestinationClicked(screen.route)
        }
        .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp, 16.dp)
        ) {
            Icon(
                painterResource(screen.iconRes),
                stringResource(screen.displayTitle),
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(screen.displayTitle),
                fontSize = 18.sp,
                color = color,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}
package com.example.chat.ui.base.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import com.example.chat.R
import io.getstream.chat.android.compose.ui.common.BackButton

@Composable
fun Toolbar(
    @StringRes title: Int?,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        BackButton(
            imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_arrow_back_24),
            onBackPressed = onBackPressed
        )
        Text(if(title != null) stringResource(title) else "", fontSize = 18.sp)
    }
}

package com.example.chat.ui.users

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.R
import com.example.chat.ui.models.Screen
import io.getstream.chat.android.compose.ui.common.BackButton
import io.getstream.chat.android.compose.ui.common.SearchInput

@Composable
fun UsersToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit
) {

    var searchExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val onBack = {
        if(searchExpanded) searchExpanded = false
        else onBackPressed()
    }

    //BackHandler(onBack = onBack)

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                BackButton(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_arrow_back_24),
                    onBackPressed = onBack
                )

                if(!searchExpanded) {
                    Text(
                        text = stringResource(Screen.Users.displayText!!),
                        fontSize = 18.sp
                    )
                }
            }

            if(searchExpanded) {
                SearchInput(
                    modifier = Modifier.fillMaxWidth(),
                    query = query,
                    label = { Text("Search") },
                    onValueChange = {
                        onQueryChange(it)
                    }
                )
            } else {
                IconButton(onClick = {
                    searchExpanded = true
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_search_24),
                        "contentDescription",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}
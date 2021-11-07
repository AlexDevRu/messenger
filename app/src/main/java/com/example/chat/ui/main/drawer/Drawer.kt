package com.example.chat.ui.main.drawer

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.chat.R
import com.example.chat.ui.base.composables.CustomIconButton
import com.example.chat.ui.models.DrawerMenuItem
import com.example.domain.models.ChatUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.models.User

private val drawerScreens = listOf(
    DrawerMenuItem.Channels,
    DrawerMenuItem.Phone,
    DrawerMenuItem.Contacts,
    DrawerMenuItem.Settings,
    DrawerMenuItem.Logout
)

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClick: (route: String) -> Unit,
    onEditProfileClick: () -> Unit = {},
    activeRoute: String,
    userIsLoading: Boolean
) {
    val constraintSet = ConstraintSet {
        val userAvatarRef = createRefFor("userAvatar")
        val editProfileButtonRef = createRefFor("editProfileButton")
        val menuItemsRef = createRefFor("menuItems")

        constrain(userAvatarRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(editProfileButtonRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end, 16.dp)
        }

        constrain(menuItemsRef) {
            top.linkTo(userAvatarRef.bottom)
        }
    }

    ConstraintLayout(
        constraintSet,
        modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        DrawerAvatar(
            modifier = Modifier.layoutId("userAvatar"),
            loading = userIsLoading
        )

        CustomIconButton(
            iconRes = R.drawable.ic_baseline_edit_24,
            modifier = Modifier.layoutId("editProfileButton"),
            onClick = onEditProfileClick
        )

        Column(
            modifier = Modifier
                .layoutId("menuItems")
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        ) {
            drawerScreens.forEach { screen ->
                NavDrawerItem(
                    screen = screen,
                    onDestinationClicked = onDestinationClick,
                    active = screen.route == activeRoute
                )
            }
        }
    }
}
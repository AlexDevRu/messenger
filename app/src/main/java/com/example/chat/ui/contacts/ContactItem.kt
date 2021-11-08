package com.example.chat.ui.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mappers.toDataModel
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    contact: Contact,
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple()
    ) {
        onClick()
    }) {

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(contact.user != null)
                UserAvatar(user = contact.user!!.toDataModel(), modifier = Modifier.size(40.dp))
            Column {
                val nameSuffix = if(contact.user != null) " - ${contact.user?.userName}" else ""
                Text(text = "${contact.contactName}${nameSuffix}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = contact.phoneNumbers.joinToString(", "), fontSize = 12.sp)
            }
        }
    }
}
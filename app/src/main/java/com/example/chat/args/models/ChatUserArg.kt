package com.example.chat.args.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ChatUserArg(
    val id: String = "",
    val email: String = "",
    val userName: String = "",
    val phone: String? = null,
    val avatar: String? = null,
    val lastActive: Date? = null
): Parcelable
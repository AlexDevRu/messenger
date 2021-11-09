package com.example.chat.utils

import android.text.format.DateFormat
import java.util.*

fun Date?.toReadableString(): String {
    return if(this?.time != null) DateFormat.format("dd/MM/yyyy hh:mm", time).toString() else ""
}
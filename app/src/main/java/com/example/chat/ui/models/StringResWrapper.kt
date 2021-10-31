package com.example.chat.ui.models

import androidx.annotation.StringRes

data class StringResWrapper(
    @StringRes val stringRes: Int,
    val formatArgs: Array<Any> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StringResWrapper

        if (stringRes != other.stringRes) return false
        if (!formatArgs.contentEquals(other.formatArgs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stringRes
        result = 31 * result + formatArgs.contentHashCode()
        return result
    }
}
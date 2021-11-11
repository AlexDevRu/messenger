package com.example.data.extensions

import io.getstream.chat.android.client.models.User

var User.email: String
    get() = extraData["email"].toString()
    set(value) {
        extraData["email"] = value
    }
package com.example.domain.models

data class ChatUser(
    var id: String = "",
    var email: String = "",
    var userName: String = "",
    var phone: String? = null,
    var avatar: Any? = null,
)

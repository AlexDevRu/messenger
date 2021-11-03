package com.example.domain.models

data class ChatUser(
    val id: String = "",
    val email: String = "",
    val userName: String = "",
    val phone: String = "",
    val avatar: String? = null,
)
package com.dinno.health_chat.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChatUserModel(
    val name: String,
    val imageUrl: String,
    val isCurrentUser: Boolean
)
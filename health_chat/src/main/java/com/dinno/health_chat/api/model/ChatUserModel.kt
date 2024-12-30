package com.dinno.health_chat.api.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChatUserModel(
    val imageUrl: String,
    val name: String,
    val description: String? = null
)
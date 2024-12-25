package com.dinno.health_chat.model

import androidx.annotation.Keep

@Keep
enum class ChatMediaType(val type: String) {
    PDF("application/pdf"),
    IMAGE("image/*")
}
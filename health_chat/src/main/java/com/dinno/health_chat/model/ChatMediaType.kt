package com.dinno.health_chat.model

import androidx.annotation.Keep

@Keep
enum class ChatMediaType(val type: String, val suffix: String) {
    PDF("application/pdf", ".pdf"),
    JPG("image/jpeg", ".jpg"),
    MP3("audio/mpeg", ".mp3")
}
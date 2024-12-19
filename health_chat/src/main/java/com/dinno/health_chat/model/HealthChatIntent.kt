package com.dinno.health_chat.model

sealed interface HealthChatIntent {
    data object OnRetry : HealthChatIntent
    data class OnTextMessageSend(val text: String) : HealthChatIntent
    data class OnMessageSendRetry(val message: ChatMessage) : HealthChatIntent
}
package com.dinno.health_chat.model

sealed interface HealthChatIntent {
    data object OnRetry : HealthChatIntent
    data class OnMessageSend(val message: ChatMessage) : HealthChatIntent
    data class OnMessageSendRetry(val message: ChatMessage) : HealthChatIntent
}
package com.dinno.health_chat.api.model

sealed interface HealthChatIntent {
    data object OnRetry : HealthChatIntent
    data object OnNavigateBack : HealthChatIntent
    data class OnNavigateToUser(val user: ChatUserModel) : HealthChatIntent
    data class OnMessageSend(val message: ChatMessage) : HealthChatIntent
    data class OnMessageSendRetry(val message: ChatMessage) : HealthChatIntent
}
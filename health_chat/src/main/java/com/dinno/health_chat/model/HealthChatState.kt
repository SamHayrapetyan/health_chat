package com.dinno.health_chat.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface HealthChatState {
    @Immutable
    data object Loading : HealthChatState

    @Immutable
    data object Error : HealthChatState

    @Immutable
    data class Active(
        val currentUser: ChatUserModel,
        val otherUser: ChatUserModel,
        val chatExpirationEpochDate: Long,
        val messages: List<ChatMessage>
    ) : HealthChatState

    @Immutable
    data class Inactive(
        val currentUser: ChatUserModel,
        val otherUser: ChatUserModel,
        val messages: List<ChatMessage>
    ) : HealthChatState
}
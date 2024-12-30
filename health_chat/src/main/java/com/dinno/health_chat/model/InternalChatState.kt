package com.dinno.health_chat.model

import androidx.compose.runtime.Immutable
import com.dinno.health_chat.api.model.ChatUserModel

@Immutable
internal sealed interface InternalChatState {
    @Immutable
    data object Loading : InternalChatState

    @Immutable
    data object Error : InternalChatState

    @Immutable
    data class Active(
        val currentUser: ChatUserModel,
        val otherUser: ChatUserModel,
        val chatExpirationDate: String,
        val messages: List<InternalChatMessage>
    ) : InternalChatState

    @Immutable
    data class Inactive(
        val currentUser: ChatUserModel,
        val otherUser: ChatUserModel,
        val messages: List<InternalChatMessage>
    ) : InternalChatState
}
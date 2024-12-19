package com.dinno.health_chat.model

import androidx.compose.runtime.Immutable

/**
 * Chat message object required to display corresponding ui.
 *
 * @throws IllegalArgumentException if id was duplicated.
 */
@Immutable
sealed class ChatMessage(
    open val id: String,
    open val sender: ChatUserModel,
    open val status: MessageStatus,
    open val text: String?,
    open val creationDateEpoch: Long
) {

    @Immutable
    data class Text(
        override val id: String,
        override val sender: ChatUserModel,
        override val text: String,
        override val creationDateEpoch: Long,
        override val status: MessageStatus
    ) : ChatMessage(id = id, sender = sender, text = text, creationDateEpoch = creationDateEpoch, status = status)

    @Immutable
    data class Media(
        override val id: String,
        override val sender: ChatUserModel,
        override val text: String,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val mediaType: ChatMediaType
    ) : ChatMessage(id = id, sender = sender, text = text, creationDateEpoch = creationDateEpoch, status = status)
}
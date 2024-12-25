package com.dinno.health_chat.model

import android.net.Uri
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
    open val creationDateEpoch: Long
) {

    @Immutable
    data class Text(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val text: String
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)

    @Immutable
    data class Media(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val uri: Uri,
        val mediaType: ChatMediaType
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)

    @Immutable
    data class Audio(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val uri: Uri,
        val isPlaying: Boolean
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)
}
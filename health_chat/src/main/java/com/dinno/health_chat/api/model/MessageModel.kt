package com.dinno.health_chat.api.model

import android.net.Uri

/**
 * Chat message object required to display corresponding ui.
 *
 * @throws IllegalArgumentException if id was duplicated.
 */
sealed class ChatMessage(
    open val id: String,
    open val sender: ChatUserModel,
    open val status: MessageStatus,
    open val creationDateEpoch: Long
) {

    data class Text(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val text: String
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)

    data class Image(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val uri: Uri,
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)

    data class File(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val uri: Uri,
        val type: ChatFileType,
        val fileName: String?,
        val fileSizeInBytes: Long?
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)

    data class Audio(
        override val id: String,
        override val sender: ChatUserModel,
        override val creationDateEpoch: Long,
        override val status: MessageStatus,
        val uri: Uri,
        val durationInMilliseconds: Long?,
    ) : ChatMessage(id = id, sender = sender, creationDateEpoch = creationDateEpoch, status = status)
}
package com.dinno.health_chat.model

import androidx.compose.runtime.Immutable
import com.dinno.health_chat.api.model.ChatMessage

@Immutable
internal sealed class InternalChatMessage(
    open val domainMessage: ChatMessage,
    open val uid: String,
    open val creationDate: String
) {

    @Immutable
    data class Text(
        override val domainMessage: ChatMessage.Text,
        override val uid: String,
        override val creationDate: String
    ) : InternalChatMessage(domainMessage = domainMessage, uid = uid, creationDate = creationDate)

    @Immutable
    data class Image(
        override val domainMessage: ChatMessage.Image,
        override val uid: String,
        override val creationDate: String
    ) : InternalChatMessage(domainMessage = domainMessage, uid = uid, creationDate = creationDate)

    @Immutable
    data class File(
        override val domainMessage: ChatMessage.File,
        override val uid: String,
        override val creationDate: String,
        val sizeInKb: String?
    ) : InternalChatMessage(domainMessage = domainMessage, uid = uid, creationDate = creationDate)

    @Immutable
    data class Audio(
        override val domainMessage: ChatMessage.Audio,
        override val uid: String,
        override val creationDate: String,
        val isPlaying: Boolean,
        val duration: String?
    ) : InternalChatMessage(domainMessage = domainMessage, uid = uid, creationDate = creationDate)
}
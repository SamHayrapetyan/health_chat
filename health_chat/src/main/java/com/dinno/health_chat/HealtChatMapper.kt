package com.dinno.health_chat

import com.dinno.health_chat.api.model.ChatMessage
import com.dinno.health_chat.api.model.HealthChatState
import com.dinno.health_chat.model.AudioPlayerState
import com.dinno.health_chat.model.InternalChatMessage
import com.dinno.health_chat.model.InternalChatState
import com.dinno.health_chat.utils.bytesToReadableString
import com.dinno.health_chat.utils.dateEpochToReadableString
import com.dinno.health_chat.utils.durationToReadableString
import com.dinno.health_chat.utils.messageDateEpochToReadableString

internal fun HealthChatState.toInternalState(currentAudioMessage: InternalChatMessage.Audio?): InternalChatState =
    when (this) {
        is HealthChatState.Error -> InternalChatState.Error
        is HealthChatState.Loading -> InternalChatState.Loading
        is HealthChatState.Inactive -> InternalChatState.Inactive(
            currentUser = currentUser,
            otherUser = otherUser,
            messages = messages.asReversed().map { it.toInternalMessage(currentAudioMessage) }
        )

        is HealthChatState.Active -> InternalChatState.Active(
            currentUser = currentUser,
            otherUser = otherUser,
            chatExpirationDate = runCatching { dateEpochToReadableString(chatExpirationEpochDate) }.getOrNull()
                .orEmpty(),
            messages = messages.asReversed().map { it.toInternalMessage(currentAudioMessage) }
        )
    }

internal fun ChatMessage.toInternalMessage(currentAudioMessage: InternalChatMessage.Audio?): InternalChatMessage =
    when (this) {
        is ChatMessage.Audio -> InternalChatMessage.Audio(
            domainMessage = this,
            creationDate = messageDateEpochToReadableString(creationDateEpoch),
            audioPlayerState = if (currentAudioMessage?.domainMessage?.id == id) currentAudioMessage.audioPlayerState else AudioPlayerState.Paused,
            duration = runCatching { durationToReadableString(durationInMilliseconds!!) }.getOrNull()
        )

        is ChatMessage.File -> InternalChatMessage.File(
            domainMessage = this,
            creationDate = messageDateEpochToReadableString(creationDateEpoch),
            sizeInKb = runCatching { bytesToReadableString(fileSizeInBytes!!) }.getOrNull(),
        )

        is ChatMessage.Image -> InternalChatMessage.Image(
            domainMessage = this,
            creationDate = messageDateEpochToReadableString(creationDateEpoch)
        )

        is ChatMessage.Text -> InternalChatMessage.Text(
            domainMessage = this,
            creationDate = messageDateEpochToReadableString(creationDateEpoch)
        )
    }
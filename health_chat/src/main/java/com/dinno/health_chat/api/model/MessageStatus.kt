package com.dinno.health_chat.api.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MessageStatus {
    @Immutable
    data object Pending : MessageStatus

    @Immutable
    data object Failed : MessageStatus

    @Immutable
    data object Sent : MessageStatus
}
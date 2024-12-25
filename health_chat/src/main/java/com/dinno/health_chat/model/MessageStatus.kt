package com.dinno.health_chat.model

sealed interface MessageStatus {
    data object Pending : MessageStatus
    data object Failed : MessageStatus
    data object Sent : MessageStatus
}
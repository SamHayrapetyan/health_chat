package com.dinno.health_chat.composables.topbar

import androidx.compose.runtime.Composable
import com.dinno.health_chat.model.HealthChatState

@Composable
internal fun HealthChatTopBar(state: HealthChatState, onBackClick: () -> Unit) {
    when (state) {
        is HealthChatState.Active -> TopBarVisibleState(
            otherUser = state.otherUser,
            chatExpirationEpochDate = state.chatExpirationEpochDate,
            onBackClick = onBackClick
        )

        is HealthChatState.Inactive -> TopBarVisibleState(
            otherUser = state.otherUser,
            chatExpirationEpochDate = null,
            onBackClick = onBackClick
        )

        is HealthChatState.Loading -> TopBarLoadingState(onBackClick = onBackClick)
        is HealthChatState.Error -> Unit
    }
}
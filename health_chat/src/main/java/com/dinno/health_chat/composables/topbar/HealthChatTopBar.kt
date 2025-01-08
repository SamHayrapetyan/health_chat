package com.dinno.health_chat.composables.topbar

import androidx.compose.runtime.Composable
import com.dinno.health_chat.model.InternalChatState

@Composable
internal fun HealthChatTopBar(state: InternalChatState, onBackClick: () -> Unit, onUserClick: () -> Unit) {
    when (state) {
        is InternalChatState.Active -> TopBarVisibleState(
            otherUser = state.otherUser,
            chatExpirationDate = state.chatExpirationDate,
            onBackClick = onBackClick,
            onUserClick = onUserClick
        )

        is InternalChatState.Inactive -> TopBarVisibleState(
            otherUser = state.otherUser,
            chatExpirationDate = null,
            onBackClick = onBackClick,
            onUserClick = onUserClick
        )

        is InternalChatState.Loading -> TopBarLoadingState(onBackClick = onBackClick)
        is InternalChatState.Error -> Unit
    }
}
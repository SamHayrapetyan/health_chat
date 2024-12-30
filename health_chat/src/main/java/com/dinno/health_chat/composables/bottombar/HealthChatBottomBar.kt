package com.dinno.health_chat.composables.bottombar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.dinno.health_chat.model.InternalChatState

@Composable
internal fun HealthChatBottomBar(
    state: InternalChatState,
    onAudioButtonPressed: () -> Unit,
    onAudioButtonReleased: () -> Unit,
    onAudioButtonCanceled: () -> Unit,
    onImageButtonClick: () -> Unit,
    onFileButtonClick: () -> Unit,
    onTextMessageSend: (String) -> Unit
) {
    Column {
        HorizontalDivider()
        when (state) {
            is InternalChatState.Loading -> BottomBarLoadingState()
            is InternalChatState.Active -> BottomBarVisibleState(
                isEnabled = true,
                onAudioButtonPressed = onAudioButtonPressed,
                onAudioButtonReleased = onAudioButtonReleased,
                onAudioButtonCanceled = onAudioButtonCanceled,
                onImageButtonClick = onImageButtonClick,
                onFileButtonClick = onFileButtonClick,
                onTextMessageSend = onTextMessageSend
            )

            is InternalChatState.Inactive -> BottomBarVisibleState(
                isEnabled = false,
                onAudioButtonPressed = onAudioButtonPressed,
                onAudioButtonReleased = onAudioButtonReleased,
                onAudioButtonCanceled = onAudioButtonCanceled,
                onImageButtonClick = onImageButtonClick,
                onFileButtonClick = onFileButtonClick,
                onTextMessageSend = onTextMessageSend
            )

            is InternalChatState.Error -> Unit
        }
    }
}
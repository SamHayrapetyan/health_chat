package com.dinno.health_chat.composables.bottombar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.dinno.health_chat.model.HealthChatState

@Composable
internal fun HealthChatBottomBar(
    state: HealthChatState,
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
            is HealthChatState.Loading -> BottomBarLoadingState()
            is HealthChatState.Active -> BottomBarVisibleState(
                isEnabled = true,
                onAudioButtonPressed = onAudioButtonPressed,
                onAudioButtonReleased = onAudioButtonReleased,
                onAudioButtonCanceled = onAudioButtonCanceled,
                onImageButtonClick = onImageButtonClick,
                onFileButtonClick = onFileButtonClick,
                onTextMessageSend = onTextMessageSend
            )

            is HealthChatState.Inactive -> BottomBarVisibleState(
                isEnabled = false,
                onAudioButtonPressed = onAudioButtonPressed,
                onAudioButtonReleased = onAudioButtonReleased,
                onAudioButtonCanceled = onAudioButtonCanceled,
                onImageButtonClick = onImageButtonClick,
                onFileButtonClick = onFileButtonClick,
                onTextMessageSend = onTextMessageSend
            )

            is HealthChatState.Error -> Unit
        }
    }
}
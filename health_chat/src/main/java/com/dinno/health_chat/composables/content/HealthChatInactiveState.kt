package com.dinno.health_chat.composables.content

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.components.ImageWithLoading
import com.dinno.health_chat.model.InternalChatMessage
import com.dinno.health_chat.model.InternalChatState

@Composable
internal fun HealthChatInactiveState(
    state: InternalChatState.Inactive,
    onPlayPauseClick: (InternalChatMessage.Audio) -> Unit,
    onImageClick: (Uri) -> Unit,
    onFileClick: (Uri) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        reverseLayout = true,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
    ) {
        items(items = state.messages, key = { it.uid }) { message ->
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val isCurrentUser = remember { message.domainMessage.sender == state.currentUser }
                if (!isCurrentUser) {
                    ImageWithLoading(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .size(32.dp),
                        url = message.domainMessage.sender.imageUrl
                    )
                }
                when (message) {
                    is InternalChatMessage.Audio -> AudioChatBubble(
                        message = message,
                        isCurrentUser = isCurrentUser,
                        onRetryMessageSendClick = {},
                        onPlayPauseClick = { onPlayPauseClick(message) },
                        modifier = Modifier.widthIn(max = 260.dp)
                    )

                    is InternalChatMessage.File -> FileChatBubble(
                        message = message,
                        isCurrentUser = isCurrentUser,
                        onRetryMessageSendClick = {},
                        onFileClick = { onFileClick(message.domainMessage.uri) },
                        modifier = Modifier.widthIn(max = 260.dp)
                    )

                    is InternalChatMessage.Image -> ImageChatBubble(
                        message = message,
                        isCurrentUser = isCurrentUser,
                        onRetryMessageSendClick = {},
                        onImageClick = { onImageClick(message.domainMessage.uri) },
                        modifier = Modifier.widthIn(max = 260.dp)
                    )

                    is InternalChatMessage.Text -> TextChatBubble(
                        message = message,
                        isCurrentUser = isCurrentUser,
                        onRetryMessageSendClick = {},
                        modifier = Modifier.widthIn(max = 260.dp)
                    )
                }
            }
        }
    }
}
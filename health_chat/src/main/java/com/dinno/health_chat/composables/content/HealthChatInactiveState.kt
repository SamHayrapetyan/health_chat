package com.dinno.health_chat.composables.content

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.dinno.health_chat.model.ChatMediaType
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.HealthChatState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HealthChatInactiveState(
    state: HealthChatState.Inactive,
    onRetryMessageSendClick: (ChatMessage) -> Unit,
    onPlayPauseClick: (ChatMessage.Audio) -> Unit,
    onImageClick: (Uri) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        reverseLayout = true,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
    ) {
        items(items = state.messages, key = { it.id }) { message ->
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val isCurrentUser = remember { message.sender == state.currentUser }
                if (!isCurrentUser) {
                    ImageWithLoading(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .size(32.dp),
                        url = message.sender.imageUrl
                    )
                }
                when (message) {
                    is ChatMessage.Media -> when (message.mediaType) {
                        ChatMediaType.PDF -> Unit
                        ChatMediaType.IMAGE -> ImageChatBubble(
                            message = message,
                            isCurrentUser = isCurrentUser,
                            onRetryMessageSendClick = { onRetryMessageSendClick(message) },
                            onImageClick = { onImageClick(message.uri) },
                            modifier = Modifier.widthIn(max = 260.dp)
                        )
                    }

                    is ChatMessage.Text -> {
                        TextChatBubble(
                            message = message,
                            isCurrentUser = isCurrentUser,
                            onRetryMessageSendClick = { onRetryMessageSendClick(message) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        )
                    }

                    is ChatMessage.Audio -> {
                        AudioChatBubble(
                            message = message,
                            isCurrentUser = isCurrentUser,
                            onRetryMessageSendClick = { onRetryMessageSendClick(message) },
                            onPlayPauseClick = { onPlayPauseClick(message) },
                            modifier = Modifier.widthIn(max = 260.dp)
                        )
                    }
                }
            }
        }
    }
}
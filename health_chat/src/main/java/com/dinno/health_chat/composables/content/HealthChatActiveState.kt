package com.dinno.health_chat.composables.content

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.components.ImageWithLoading
import com.dinno.health_chat.model.ChatMediaType
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.HealthChatState
import com.dinno.health_chat.model.MessageStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
internal fun HealthChatActiveState(
    state: HealthChatState.Active,
    onRetryMessageSendClick: (ChatMessage) -> Unit,
    onPlayPauseClick: (ChatMessage.Audio) -> Unit,
    onImageClick: (Uri) -> Unit,
    onFileClick: (Uri) -> Unit
) {
    val lastMessage by remember(state.messages) { derivedStateOf { state.messages.firstOrNull() } }

    val listState = rememberLazyListState()

    var showScrollToBottomButton by remember { mutableStateOf(false) }
    LaunchedEffect(lastMessage) {
        when {
            lastMessage?.sender == state.currentUser && lastMessage?.status !is MessageStatus.Failed -> {
                listState.animateScrollToItem(0)
            }

            lastMessage?.sender == state.otherUser -> {
                if (listState.firstVisibleItemIndex in 0..3) listState.animateScrollToItem(0)
                else showScrollToBottomButton = true
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.distinctUntilChanged().collectLatest { index ->
            if (index == 0) showScrollToBottomButton = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        when (message) {
                            is ChatMessage.Media -> when (message.mediaType) {
                                ChatMediaType.PDF -> FileChatBubble(
                                    message = message,
                                    isCurrentUser = isCurrentUser,
                                    onRetryMessageSendClick = { onRetryMessageSendClick(message) },
                                    onFileClick = { onFileClick(message.uri) },
                                    modifier = Modifier.widthIn(max = 260.dp)
                                )

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
                                    modifier = Modifier.widthIn(max = 260.dp)
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
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd),
            visible = showScrollToBottomButton,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val scope = rememberCoroutineScope()
            Surface(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                shape = CircleShape,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
            }
        }
    }
}
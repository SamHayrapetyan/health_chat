package com.dinno.health_chat.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.HealthChatState
import com.dinno.health_chat.model.MessageStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HealthChatActiveState(state: HealthChatState.Active, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = false,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = state.messages, key = { it.id }) { message ->
            when (message) {
                is ChatMessage.Media -> Text("Not supported")
                is ChatMessage.Text -> ChatBubble(
                    message = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage.Text, modifier: Modifier = Modifier) {
    val textColor by animateColorAsState(
        when (message.status) {
            MessageStatus.Failed -> MaterialTheme.colorScheme.onError
            MessageStatus.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
            MessageStatus.Read -> MaterialTheme.colorScheme.onSurface
            MessageStatus.Sent -> MaterialTheme.colorScheme.onSurface
        },
        label = "chat text color animation"
    )
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(if (message.sender.isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (message.sender.isCurrentUser) 48f else 0f,
                        bottomEnd = if (message.sender.isCurrentUser) 0f else 48f
                    )
                )
                .background(if (message.sender.isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
        ) { Text(text = message.text, color = textColor) }
    }
}
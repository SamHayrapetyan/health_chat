package com.dinno.health_chat.composables.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.R
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.MessageStatus
import com.dinno.health_chat.utils.getFileName
import com.dinno.health_chat.utils.getFileSize
import com.dinno.health_chat.utils.toReadableString

@Composable
internal fun FileChatBubble(
    message: ChatMessage.Media,
    isCurrentUser: Boolean,
    onRetryMessageSendClick: () -> Unit,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isCurrentUser) Color(0xFFFFFFFF)
    else Color(0xFF374151)

    val backgroundColor = if (isCurrentUser) Color(0xFF0E2D6B)
    else Color(0xFFF1F5F9)
    Row(modifier = modifier) {
        if (isCurrentUser && message.status is MessageStatus.Failed) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(12.dp)
                    .size(24.dp),
                onClick = onRetryMessageSendClick,
                enabled = true
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Replay,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onFileClick)
                .background(backgroundColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color(0x1AF8FAFC), MaterialTheme.shapes.small)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier.height(40.dp),
                    imageVector = Icons.Rounded.FilePresent,
                    tint = textColor,
                    contentDescription = null
                )
                Column(verticalArrangement = Arrangement.Center) {
                    val context = LocalContext.current
                    remember { context.getFileName(message.uri) }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    remember { context.getFileSize(message.uri) }?.let {
                        Text(
                            text = stringResource(R.string.hc_kb_template).format(it / 1024),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.align(if (isCurrentUser) Alignment.End else Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                if (isCurrentUser) {
                    when (message.status) {
                        MessageStatus.Pending -> CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.dp,
                            strokeCap = StrokeCap.Round,
                            color = textColor,
                            trackColor = backgroundColor
                        )

                        MessageStatus.Failed -> Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Rounded.ErrorOutline,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = null
                        )

                        MessageStatus.Sent -> Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Rounded.Check,
                            tint = textColor,
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = message.creationDateEpoch.toReadableString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
            }
        }
    }
}
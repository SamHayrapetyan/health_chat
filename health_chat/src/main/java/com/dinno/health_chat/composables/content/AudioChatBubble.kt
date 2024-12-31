package com.dinno.health_chat.composables.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.R
import com.dinno.health_chat.api.model.MessageStatus
import com.dinno.health_chat.model.AudioPlayerState
import com.dinno.health_chat.model.InternalChatMessage

@Composable
internal fun AudioChatBubble(
    message: InternalChatMessage.Audio,
    isCurrentUser: Boolean,
    onRetryMessageSendClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isCurrentUser) Color(0xFFFFFFFF)
    else Color(0xFF374151)

    val backgroundColor = if (isCurrentUser) Color(0xFF0E2D6B)
    else Color(0xFFF1F5F9)
    Row(modifier = modifier) {
        if (isCurrentUser && message.domainMessage.status is MessageStatus.Failed) {
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
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color(0x1AF8FAFC), MaterialTheme.shapes.small)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    onClick = onPlayPauseClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    AnimatedContent(targetState = message.audioPlayerState, label = "button icon transition") {
                        when (it) {
                            AudioPlayerState.Loading -> CircularProgressIndicator(
                                modifier = Modifier.padding(8.dp),
                                strokeWidth = 1.dp,
                                strokeCap = StrokeCap.Round,
                                color = backgroundColor,
                                trackColor = textColor
                            )

                            AudioPlayerState.Paused -> Icon(
                                modifier = Modifier.padding(8.dp),
                                imageVector = Icons.Rounded.PlayArrow,
                                tint = Color(0xFF0E2D6B),
                                contentDescription = null
                            )

                            AudioPlayerState.Playing -> Icon(
                                modifier = Modifier.padding(8.dp),
                                imageVector = Icons.Rounded.Pause,
                                tint = Color(0xFF0E2D6B),
                                contentDescription = null
                            )
                        }
                    }
                }
                Icon(
                    modifier = Modifier
                        .height(64.dp)
                        .weight(weight = 1f, fill = false),
                    painter = painterResource(R.drawable.hc_ic_audio_wave),
                    tint = textColor,
                    contentDescription = null
                )
                message.duration?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            }
            Row(
                modifier = Modifier.align(if (isCurrentUser) Alignment.End else Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                if (isCurrentUser) {
                    when (message.domainMessage.status) {
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
                    text = message.creationDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
            }
        }
    }
}
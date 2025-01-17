package com.dinno.health_chat.composables.bottombar

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.R
import com.dinno.health_chat.components.MessageField
import com.dinno.health_chat.components.RecordButton
import com.dinno.health_chat.components.RecordingIndicator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun BottomBarVisibleState(
    isEnabled: Boolean,
    onAudioButtonPressed: () -> Unit,
    onAudioButtonReleased: () -> Unit,
    onAudioButtonCanceled: () -> Unit,
    onImageButtonClick: () -> Unit,
    onFileButtonClick: () -> Unit,
    onTextMessageSend: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
        var text by rememberSaveable { mutableStateOf("") }
        val isTextBlank by remember { derivedStateOf { text.isBlank() } }

        var swipeOffset by remember { mutableFloatStateOf(0f) }
        var isRecordingMessage by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .background(shape = MaterialTheme.shapes.extraLarge, color = Color(0xFFF1F5F9))
                .weight(weight = 1f, fill = true)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecordingMessage) {
                RecordingIndicator(swipeOffset = { swipeOffset })
            } else {
                val color = if (isEnabled) Color(0xFF374151) else Color(0xFF94A3B8)
                MessageField(
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .weight(weight = 1f, fill = true),
                    query = text,
                    onQueryChange = { text = it },
                    enabled = isEnabled,
                    placeholder = stringResource(R.string.hc_message)
                )
//                AnimatedVisibility(modifier = Modifier.width(88.dp), visible = isTextBlank) {
                AnimatedVisibility(modifier = Modifier.width(64.dp), visible = isTextBlank) {
//                    Row(
//                        modifier = Modifier.padding(end = 8.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy((-12).dp)
//                    ) {
                    IconButton(
                        onClick = onImageButtonClick,
                        enabled = isEnabled,
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Rounded.Image,
                            tint = color,
                            contentDescription = null
                        )
                    }
//                        IconButton(
//                            onClick = onFileButtonClick,
//                            enabled = isEnabled,
//                        ) {
//                            Icon(
//                                modifier = Modifier.size(24.dp),
//                                imageVector = Icons.Rounded.AttachFile,
//                                tint = color,
//                                contentDescription = null
//                            )
//                        }
//                }
                }
            }
        }
        if (isEnabled) {
            AnimatedContent(
                targetState = isTextBlank,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "Message send button transition"
            ) {
                if (it) {
                    RecordButton(
                        recording = isRecordingMessage,
                        swipeOffset = { swipeOffset },
                        onSwipeOffsetChange = { offset -> swipeOffset = offset },
                        onStartRecording = {
                            if (recordAudioPermissionState.status.isGranted) {
                                val consumed = !isRecordingMessage
                                isRecordingMessage = true
                                onAudioButtonPressed()
                                consumed
                            } else {
                                recordAudioPermissionState.launchPermissionRequest()
                                false
                            }
                        },
                        onFinishRecording = {
                            onAudioButtonReleased()
                            isRecordingMessage = false
                        },
                        onCancelRecording = {
                            onAudioButtonCanceled()
                            isRecordingMessage = false
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = null,
                        tint = Color(0xFF0E2D6B),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    onTextMessageSend(text)
                                    text = ""
                                },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .size(56.dp)
                            .padding(16.dp)
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier
                    .size(56.dp)
                    .padding(16.dp)
            )
        }
    }
}
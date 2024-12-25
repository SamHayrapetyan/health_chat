package com.dinno.health_chat

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinno.health_chat.api.HealthChatManager
import com.dinno.health_chat.audio.AudioPlayer
import com.dinno.health_chat.audio.AudioRecorder
import com.dinno.health_chat.model.ChatMediaType
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.HealthChatIntent
import com.dinno.health_chat.model.HealthChatState
import com.dinno.health_chat.model.MessageStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

internal class HealthChatViewModel(
    private val chatManager: HealthChatManager,
    private val appContext: Context,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<HealthChatState> = MutableStateFlow(HealthChatState.Loading)
    private val _managerFlow = chatManager.getChatState()
        .map { (it as? HealthChatState.Active)?.copy(messages = it.messages.asReversed()) ?: it }
    val stateFlow = merge(_stateFlow, _managerFlow).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HealthChatState.Loading
    )

    init {
        audioPlayer.addCompletionListener {
            viewModelScope.launch {
                _stateFlow.update { state ->
                    (state as? HealthChatState.Active)?.let {
                        it.copy(messages = it.messages.map { innerMessage ->
                            (innerMessage as? ChatMessage.Audio)?.copy(isPlaying = false) ?: innerMessage
                        })
                    } ?: (state as? HealthChatState.Inactive)?.let {
                        it.copy(messages = it.messages.map { innerMessage ->
                            (innerMessage as? ChatMessage.Audio)?.copy(isPlaying = false) ?: innerMessage
                        })
                    } ?: state
                }
            }
        }
    }

    fun onTextMessageSend(text: String) {
        viewModelScope.launch {
            chatManager.handleIntent(
                HealthChatIntent.OnMessageSend(
                    ChatMessage.Text(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateEpoch = System.currentTimeMillis(),
                        status = MessageStatus.Pending,
                        text = text
                    )
                )
            )
        }
    }

    fun onImageMessageSend(uri: Uri) {
        viewModelScope.launch {
            chatManager.handleIntent(
                HealthChatIntent.OnMessageSend(
                    ChatMessage.Media(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateEpoch = System.currentTimeMillis(),
                        status = MessageStatus.Pending,
                        uri = uri,
                        mediaType = ChatMediaType.IMAGE
                    )
                )
            )
        }
    }

    fun onFileMessageSend(uri: Uri) {
        viewModelScope.launch {
            chatManager.handleIntent(
                HealthChatIntent.OnMessageSend(
                    ChatMessage.Media(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateEpoch = System.currentTimeMillis(),
                        status = MessageStatus.Pending,
                        uri = uri,
                        mediaType = ChatMediaType.PDF
                    )
                )
            )
        }
    }

    private var recordingJob: Job? = null
    private var audioFile: File? = null
    fun onStartAudioRecording() {
        if (recordingJob?.isActive == true) return
        recordingJob = viewModelScope.launch {
            runSuspendCatching {
                File.createTempFile("temp_audio", ".mp3", appContext.cacheDir).also {
                    audioRecorder.start(it)
                    audioFile = it
                }
            }
        }
    }

    fun onStopAudioRecording() {
        viewModelScope.launch {
            runSuspendCatching {
                audioRecorder.stop()
                chatManager.handleIntent(
                    HealthChatIntent.OnMessageSend(
                        ChatMessage.Audio(
                            id = UUID.randomUUID().toString(),
                            sender = stateFlow.active()?.currentUser ?: return@launch,
                            creationDateEpoch = System.currentTimeMillis(),
                            status = MessageStatus.Pending,
                            uri = audioFile?.toUri() ?: return@launch,
                            isPlaying = false
                        )
                    )
                )
            }
        }
    }

    fun onCancelAudioRecording() {
        viewModelScope.launch { runSuspendCatching { audioRecorder.stop() } }
    }

    fun onPlayPauseClick(message: ChatMessage.Audio) {
        viewModelScope.launch {
            runSuspendCatching {
                audioPlayer.stop()
                _stateFlow.update {
                    (stateFlow.value as? HealthChatState.Active)?.let {
                        it.copy(messages = it.messages.map { innerMessage ->
                            (innerMessage as? ChatMessage.Audio)?.copy(isPlaying = message.id == innerMessage.id && !innerMessage.isPlaying)
                                ?: innerMessage
                        })
                    } ?: (stateFlow.value as? HealthChatState.Inactive)?.let {
                        it.copy(messages = it.messages.map { innerMessage ->
                            (innerMessage as? ChatMessage.Audio)?.copy(isPlaying = message.id == innerMessage.id && !innerMessage.isPlaying)
                                ?: innerMessage
                        })
                    } ?: stateFlow.value
                }
                if (message.isPlaying) audioPlayer.stop() else audioPlayer.playUri(message.uri)
            }
        }
    }

    fun onRetryClick() {
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnRetry) }
    }

    fun onMessageSendRetry(message: ChatMessage) {
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnMessageSendRetry(message)) }
    }

    private fun StateFlow<HealthChatState>.active() = (value as? HealthChatState.Active)

    override fun onCleared() {
        super.onCleared()
        audioPlayer.removeListeners()
    }
}

private suspend inline fun <R> runSuspendCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
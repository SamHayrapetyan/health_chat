package com.dinno.health_chat

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinno.health_chat.api.HealthChatManager
import com.dinno.health_chat.api.model.ChatFileType
import com.dinno.health_chat.api.model.ChatMessage
import com.dinno.health_chat.api.model.HealthChatIntent
import com.dinno.health_chat.api.model.MessageStatus
import com.dinno.health_chat.audio.AudioPlayer
import com.dinno.health_chat.audio.AudioPlayerListener
import com.dinno.health_chat.audio.AudioRecorder
import com.dinno.health_chat.model.AudioPlayerState
import com.dinno.health_chat.model.InternalChatMessage
import com.dinno.health_chat.model.InternalChatState
import com.dinno.health_chat.utils.getAudioLength
import com.dinno.health_chat.utils.getFileName
import com.dinno.health_chat.utils.getFileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

internal class HealthChatViewModel(
    private val chatManager: HealthChatManager,
    private val appContext: Context,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private var currentAudioMessage: InternalChatMessage.Audio? = null

    private val _stateFlow: MutableStateFlow<InternalChatState> = MutableStateFlow(InternalChatState.Loading)
    private val _managerFlow = chatManager.getChatState().map {
        runCatching { it.toInternalState(currentAudioMessage) }.getOrElse { InternalChatState.Error }
    }
    val stateFlow = merge(_stateFlow, _managerFlow).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = InternalChatState.Loading
    )

    init {
        audioPlayer.addCompletionListener(
            AudioPlayerListener(
                onStart = {
                    viewModelScope.launch {
                        _stateFlow.update {
                            stateFlow.active()?.let { activeState ->
                                activeState.copy(messages = activeState.messages.map { innerMessage ->
                                    if (innerMessage.domainMessage.id == currentAudioMessage?.domainMessage?.id) {
                                        (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Playing)
                                            ?.also { currentAudioMessage = it }
                                            ?: innerMessage
                                    } else {
                                        (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                            ?: innerMessage
                                    }
                                })
                            } ?: stateFlow.inActive()?.let { inActiveState ->
                                inActiveState.copy(messages = inActiveState.messages.map { innerMessage ->
                                    if (innerMessage.domainMessage.id == currentAudioMessage?.domainMessage?.id) {
                                        (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Playing)
                                            ?.also { currentAudioMessage = it }
                                            ?: innerMessage
                                    } else {
                                        (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                            ?: innerMessage
                                    }
                                })
                            } ?: stateFlow.value
                        }
                    }
                },
                onStop = {
                    viewModelScope.launch {
                        currentAudioMessage = null
                        _stateFlow.update {
                            stateFlow.active()?.let {
                                it.copy(messages = it.messages.map { innerMessage ->
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                })
                            } ?: stateFlow.inActive()?.let {
                                it.copy(messages = it.messages.map { innerMessage ->
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                })
                            } ?: stateFlow.value
                        }
                    }
                },
                onComplete = {
                    viewModelScope.launch {
                        currentAudioMessage = null
                        _stateFlow.update {
                            stateFlow.active()?.let {
                                it.copy(messages = it.messages.map { innerMessage ->
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                })
                            } ?: stateFlow.inActive()?.let {
                                it.copy(messages = it.messages.map { innerMessage ->
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                })
                            } ?: stateFlow.value
                        }
                    }
                }
            )
        )
    }

    fun onTextMessageSend(text: String) {
        viewModelScope.launch {
            chatManager.handleIntent(
                HealthChatIntent.OnMessageSend(
                    ChatMessage.Text(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateMillis = System.currentTimeMillis(),
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
                    ChatMessage.Image(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateMillis = System.currentTimeMillis(),
                        status = MessageStatus.Pending,
                        uri = uri
                    )
                )
            )
        }
    }

    fun onFileMessageSend(uri: Uri) {
        viewModelScope.launch {
            chatManager.handleIntent(
                HealthChatIntent.OnMessageSend(
                    ChatMessage.File(
                        id = UUID.randomUUID().toString(),
                        sender = stateFlow.active()?.currentUser ?: return@launch,
                        creationDateMillis = System.currentTimeMillis(),
                        status = MessageStatus.Pending,
                        uri = uri,
                        type = ChatFileType.PDF,
                        fileName = appContext.getFileName(uri),
                        fileSizeInBytes = appContext.getFileSize(uri)
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
                File.createTempFile("temp_audio", ".aac", appContext.cacheDir).also {
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
                val uri = audioFile?.toUri() ?: return@launch
                chatManager.handleIntent(
                    HealthChatIntent.OnMessageSend(
                        ChatMessage.Audio(
                            id = UUID.randomUUID().toString(),
                            sender = stateFlow.active()?.currentUser ?: return@launch,
                            creationDateMillis = System.currentTimeMillis(),
                            status = MessageStatus.Pending,
                            uri = uri,
                            durationInMilliseconds = appContext.getAudioLength(uri)?.toLongOrNull()
                        )
                    )
                )
            }
        }
    }

    fun onCancelAudioRecording() {
        viewModelScope.launch { runSuspendCatching { audioRecorder.stop() } }
    }

    fun onPlayPauseClick(message: InternalChatMessage.Audio) {
        viewModelScope.launch {
            runSuspendCatching {
                if (message.audioPlayerState is AudioPlayerState.Paused) {
                    _stateFlow.update {
                        stateFlow.active()?.let { activeState ->
                            activeState.copy(messages = activeState.messages.map { innerMessage ->
                                if (innerMessage.domainMessage.id == message.domainMessage.id) {
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Loading)
                                        ?.also { currentAudioMessage = it }
                                        ?: innerMessage
                                } else {
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                }
                            })
                        } ?: stateFlow.inActive()?.let { inActiveState ->
                            inActiveState.copy(messages = inActiveState.messages.map { innerMessage ->
                                if (innerMessage.domainMessage.id == message.domainMessage.id) {
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Loading)
                                        ?.also { currentAudioMessage = it }
                                        ?: innerMessage
                                } else {
                                    (innerMessage as? InternalChatMessage.Audio)?.copy(audioPlayerState = AudioPlayerState.Paused)
                                        ?: innerMessage
                                }
                            })
                        } ?: stateFlow.value
                    }
                }
                withContext(Dispatchers.IO) {
                    if (message.audioPlayerState is AudioPlayerState.Paused) audioPlayer.playUri(message.domainMessage.uri)
                    else audioPlayer.stop()
                }
            }
        }
    }

    fun onRetryClick() {
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnRetry) }
    }

    fun onMessageSendRetry(message: InternalChatMessage) {
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnMessageSendRetry(message.domainMessage)) }
    }

    fun onNavigateToUser() {
        val user = stateFlow.active()?.otherUser ?: stateFlow.inActive()?.otherUser ?: return
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnNavigateToUser(user)) }
    }

    fun onNavigateBack() {
        viewModelScope.launch { chatManager.handleIntent(HealthChatIntent.OnNavigateBack) }
    }

    private fun StateFlow<InternalChatState>.active() = (value as? InternalChatState.Active)
    private fun StateFlow<InternalChatState>.inActive() = (value as? InternalChatState.Inactive)

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
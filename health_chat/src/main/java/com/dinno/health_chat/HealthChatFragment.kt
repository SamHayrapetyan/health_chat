package com.dinno.health_chat

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinno.health_chat.api.HealthChatManagerHolder
import com.dinno.health_chat.audio.AudioPlayer
import com.dinno.health_chat.audio.AudioRecorder
import com.dinno.health_chat.components.viewModelFactory

class HealthChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val viewModel: HealthChatViewModel = viewModel(factory = viewModelFactory {
                    HealthChatViewModel(
                        chatManager = HealthChatManagerHolder.manager!!,
                        appContext = context.applicationContext,
                        audioRecorder = AudioRecorder(context.applicationContext),
                        audioPlayer = AudioPlayer(context.applicationContext)
                    )
                })
                val state by viewModel.stateFlow.collectAsStateWithLifecycle()
                HealthChatScreen(
                    state = state,
                    onAudioButtonPressed = viewModel::onStartAudioRecording,
                    onAudioButtonReleased = viewModel::onStopAudioRecording,
                    onAudioButtonCanceled = viewModel::onCancelAudioRecording,
                    onPlayPauseClick = viewModel::onPlayPauseClick,
                    onTextMessageSend = viewModel::onTextMessageSend,
                    onImageMessageSend = viewModel::onImageMessageSend,
                    onFileMessageSend = viewModel::onFileMessageSend,
                    onRetryClick = viewModel::onRetryClick,
                    onMessageSendRetryClick = viewModel::onMessageSendRetry,
                    onNavigateBack = { activity?.onBackPressedDispatcher?.onBackPressed() }
                )
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    companion object {
        fun newInstance() = HealthChatFragment()
    }
}
package com.dinno.health_chat

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.dinno.health_chat.api.model.ChatFileType
import com.dinno.health_chat.components.FullScreenImage
import com.dinno.health_chat.composables.bottombar.HealthChatBottomBar
import com.dinno.health_chat.composables.content.HealthChatActiveState
import com.dinno.health_chat.composables.content.HealthChatErrorState
import com.dinno.health_chat.composables.content.HealthChatInactiveState
import com.dinno.health_chat.composables.content.HealthChatLoadingState
import com.dinno.health_chat.composables.topbar.HealthChatTopBar
import com.dinno.health_chat.model.InternalChatMessage
import com.dinno.health_chat.model.InternalChatState
import com.dinno.health_chat.modifier.unFocusOnTap

@Composable
internal fun HealthChatScreen(
    state: InternalChatState,
    onAudioButtonPressed: () -> Unit,
    onAudioButtonReleased: () -> Unit,
    onAudioButtonCanceled: () -> Unit,
    onTextMessageSend: (String) -> Unit,
    onImageMessageSend: (Uri) -> Unit,
    onFileMessageSend: (Uri) -> Unit,
    onRetryClick: () -> Unit,
    onMessageSendRetryClick: (InternalChatMessage) -> Unit,
    onPlayPauseClick: (InternalChatMessage.Audio) -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> uri?.let { onImageMessageSend(uri) } },
    )
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? -> uri?.let { onFileMessageSend(uri) } },
    )
    var openedPhotoUri: Uri? by remember { mutableStateOf(null) }
    var openedFileUri: Uri? by remember { mutableStateOf(null) }

    BackHandler(openedPhotoUri != null) { openedPhotoUri = null }

    Scaffold(
        modifier = modifier
            .unFocusOnTap()
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding(),
        containerColor = Color(0xFFFFFFFF),
        topBar = { HealthChatTopBar(state = state, onBackClick = onNavigateBack, onUserClick = onNavigateToUser) },
        bottomBar = {
            HealthChatBottomBar(
                state = state,
                onTextMessageSend = onTextMessageSend,
                onAudioButtonPressed = onAudioButtonPressed,
                onAudioButtonReleased = onAudioButtonReleased,
                onAudioButtonCanceled = onAudioButtonCanceled,
                onImageButtonClick = { photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onFileButtonClick = { filePickerLauncher.launch(arrayOf(ChatFileType.PDF.type)) },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (state) {
                is InternalChatState.Loading -> HealthChatLoadingState()
                is InternalChatState.Error -> HealthChatErrorState(onRetryClick = { onRetryClick() })
                is InternalChatState.Active -> HealthChatActiveState(
                    state = state,
                    onRetryMessageSendClick = onMessageSendRetryClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onImageClick = { openedPhotoUri = it },
                    onFileClick = { openedFileUri = it }
                )

                is InternalChatState.Inactive -> HealthChatInactiveState(
                    state = state,
                    onPlayPauseClick = onPlayPauseClick,
                    onImageClick = { openedPhotoUri = it },
                    onFileClick = { openedFileUri = it }
                )
            }
        }
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = openedPhotoUri != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LocalFocusManager.current.clearFocus()
        FullScreenImage(url = openedPhotoUri, onDismiss = { openedPhotoUri = null })
    }

    val context = LocalContext.current
    val unableToOpenFileText = stringResource(R.string.hc_unable_to_open_file)
    LaunchedEffect(openedFileUri) {
        if (openedFileUri == null) return@LaunchedEffect
        runCatching {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(openedFileUri, ChatFileType.PDF.type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(Intent.createChooser(this, "Open file with"))
            }
        }.onFailure {
            Toast.makeText(context, unableToOpenFileText, Toast.LENGTH_SHORT).show()
        }
        openedFileUri = null
    }
}
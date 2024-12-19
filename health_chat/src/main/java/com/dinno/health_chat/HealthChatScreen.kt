package com.dinno.health_chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.composables.HealthChatActiveState
import com.dinno.health_chat.composables.HealthChatErrorState
import com.dinno.health_chat.composables.HealthChatLoadingState
import com.dinno.health_chat.model.HealthChatIntent
import com.dinno.health_chat.model.HealthChatState
import com.dinno.health_chat.modifier.unFocusOnTap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HealthChatScreen(
    state: HealthChatState,
    onNewIntent: (HealthChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .unFocusOnTap()
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.chat)) }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var text by rememberSaveable { mutableStateOf("") }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    enabled = state is HealthChatState.Active,
                    singleLine = false
                )
                TextButton(
                    enabled = state is HealthChatState.Active && text.isNotBlank(),
                    onClick = {
                        onNewIntent(HealthChatIntent.OnTextMessageSend(text))
                        text = ""
                    }
                ) { Text(stringResource(R.string.send)) }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (state) {
                is HealthChatState.Loading -> HealthChatLoadingState()
                is HealthChatState.Error -> HealthChatErrorState(onRetryClick = { onNewIntent(HealthChatIntent.OnRetry) })
                is HealthChatState.Active -> HealthChatActiveState(state = state)
            }
        }
    }
}
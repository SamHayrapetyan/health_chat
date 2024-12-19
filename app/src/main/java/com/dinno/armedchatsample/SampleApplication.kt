package com.dinno.armedchatsample

import android.app.Application
import com.dinno.health_chat.api.HealthChatManager
import com.dinno.health_chat.api.HealthChatManagerHolder
import com.dinno.health_chat.model.ChatMessage
import com.dinno.health_chat.model.ChatUserModel
import com.dinno.health_chat.model.HealthChatIntent
import com.dinno.health_chat.model.HealthChatState
import com.dinno.health_chat.model.MessageStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class SampleApplication : Application() {

    private val _state: MutableStateFlow<HealthChatState> = MutableStateFlow(HealthChatState.Loading)

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            _state.update { HealthChatState.Active(emptyList()) }
        }
        HealthChatManagerHolder.initManager(object : HealthChatManager {

            override fun getChatState(): Flow<HealthChatState> = _state

            override fun handleIntent(intent: HealthChatIntent) {
                when (intent) {
                    is HealthChatIntent.OnTextMessageSend -> _state.update {
                        (it as? HealthChatState.Active)?.let {
                            it.copy(
                                messages = it.messages + ChatMessage.Text(
                                    id = UUID.randomUUID().toString(),
                                    sender = ChatUserModel("Name", "", true),
                                    text = intent.text,
                                    creationDateEpoch = Calendar.getInstance().timeInMillis,
                                    status = MessageStatus.Sent
                                )
                            )
                        } ?: it
                    }

                    is HealthChatIntent.OnMessageSendRetry -> Unit
                    is HealthChatIntent.OnRetry -> Unit
                }
            }

        })
    }
}
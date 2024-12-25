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
import java.util.UUID

class SampleApplication : Application() {

    private val _state: MutableStateFlow<HealthChatState> = MutableStateFlow(HealthChatState.Loading)

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            val otherUser = ChatUserModel(
                imageUrl = "https://gratisography.com/wp-content/uploads/2024/10/gratisography-cool-cat-800x525.jpg",
                name = "Bjishk Bjishkyan",
                description = "Bjishk CJSC"
            )
            _state.update {
                HealthChatState.Active(
                    currentUser = ChatUserModel(
                        imageUrl = "https://letsenhance.io/static/8f5e523ee6b2479e26ecc91b9c25261e/1015f/MainAfter.jpg",
                        name = "Current Useryan"
                    ),
                    otherUser = otherUser,
                    chatExpirationEpochDate = System.currentTimeMillis() + 1000 * 60 * 60 * 48,
                    messages = listOf(
                        ChatMessage.Text(
                            id = UUID.randomUUID().toString(),
                            sender = otherUser,
                            text = "Bjishk CJSC",
                            creationDateEpoch = System.currentTimeMillis(),
                            status = MessageStatus.Sent
                        ),
                    )
                )
            }
        }
        HealthChatManagerHolder.initManager(
            object : HealthChatManager {
                override fun getChatState(): Flow<HealthChatState> = _state

                override suspend fun handleIntent(intent: HealthChatIntent) {
                    when (intent) {
                        is HealthChatIntent.OnMessageSend -> when (val message = intent.message) {
                            is ChatMessage.Media -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }

                            is ChatMessage.Text -> {
                                _state.update {
                                    val newMessage = message.copy(status = MessageStatus.Pending)
                                    (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                                }
                                delay(300)
                                _state.update {
                                    val state = (it as? HealthChatState.Active) ?: return
                                    val newMessage = message.copy(status = MessageStatus.Sent)

                                    val otherUserMessage =
                                        newMessage.copy(id = UUID.randomUUID().toString(), sender = state.otherUser)
                                    state.copy(messages = state.messages.dropLast(1) + newMessage + otherUserMessage)
                                }
                            }

                            is ChatMessage.Audio -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }
                        }

                        is HealthChatIntent.OnMessageSendRetry -> when (val message = intent.message) {
                            is ChatMessage.Media -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }

                            is ChatMessage.Text -> {
                                _state.update {
                                    (it as? HealthChatState.Active)?.copy(
                                        messages = it.messages.map { message ->
                                            if (message.id == intent.message.id && intent.message.status is MessageStatus.Failed) {
                                                (intent.message as ChatMessage.Text).copy(status = MessageStatus.Pending)
                                            } else {
                                                message
                                            }
                                        }
                                    ) ?: it
                                }
                            }

                            is ChatMessage.Audio -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }
                        }

                        is HealthChatIntent.OnRetry -> Unit
                    }
                }
            }
        )
    }
}
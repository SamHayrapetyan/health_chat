package com.dinno.armedchatsample

import android.app.Application
import android.widget.Toast
import androidx.core.net.toUri
import com.dinno.health_chat.api.HealthChatManager
import com.dinno.health_chat.api.HealthChatManagerHolder
import com.dinno.health_chat.api.model.ChatFileType
import com.dinno.health_chat.api.model.ChatMessage
import com.dinno.health_chat.api.model.ChatUserModel
import com.dinno.health_chat.api.model.HealthChatIntent
import com.dinno.health_chat.api.model.HealthChatState
import com.dinno.health_chat.api.model.MessageStatus
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
                    chatExpirationDateMillis = System.currentTimeMillis() + 60 * 60 * 48 * 1000,
                    messages = listOf(
                        ChatMessage.Text(
                            id = UUID.randomUUID().toString(),
                            sender = otherUser,
                            text = "Bjishk CJSC",
                            creationDateMillis = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                            status = MessageStatus.Sent
                        ),
                        ChatMessage.Audio(
                            id = UUID.randomUUID().toString(),
                            sender = otherUser,
                            creationDateMillis = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                            status = MessageStatus.Sent,
                            uri = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg".toUri(),
                            durationInMilliseconds = null
                        ),
                        ChatMessage.File(
                            id = UUID.randomUUID().toString(),
                            sender = otherUser,
                            creationDateMillis = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                            status = MessageStatus.Sent,
                            uri = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf".toUri(),
                            fileName = "dummy.pdf",
                            fileSizeInBytes = null,
                            type = ChatFileType.PDF
                        ),
                        ChatMessage.Image(
                            id = UUID.randomUUID().toString(),
                            sender = otherUser,
                            creationDateMillis = System.currentTimeMillis() - 60 * 60 * 48 * 1000,
                            status = MessageStatus.Sent,
                            uri = "https://gratisography.com/wp-content/uploads/2024/10/gratisography-cool-cat-800x525.jpg".toUri()
                        )
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
                            is ChatMessage.Image -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Failed)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }

                            is ChatMessage.File -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages + newMessage) ?: it
                            }

                            is ChatMessage.Audio -> _state.update {
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
                                    state.copy(messages = state.messages.minus(message) + newMessage + otherUserMessage)
                                }
                            }
                        }

                        is HealthChatIntent.OnMessageSendRetry -> when (val message = intent.message) {
                            is ChatMessage.Image -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages.minus(message) + newMessage)
                                    ?: it
                            }

                            is ChatMessage.File -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages.minus(message) + newMessage)
                                    ?: it
                            }

                            is ChatMessage.Audio -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages.minus(message) + newMessage)
                                    ?: it
                            }

                            is ChatMessage.Text -> _state.update {
                                val newMessage = message.copy(status = MessageStatus.Sent)
                                (it as? HealthChatState.Active)?.copy(messages = it.messages.minus(message) + newMessage)
                                    ?: it
                            }
                        }

                        is HealthChatIntent.OnNavigateBack -> {
                            Toast.makeText(this@SampleApplication, "Back triggered", Toast.LENGTH_SHORT).show()
                        }

                        is HealthChatIntent.OnNavigateToUser -> {
                            Toast.makeText(this@SampleApplication, "User clicked", Toast.LENGTH_SHORT).show()
                        }

                        is HealthChatIntent.OnRetry -> Unit
                    }
                }
            }
        )
    }
}
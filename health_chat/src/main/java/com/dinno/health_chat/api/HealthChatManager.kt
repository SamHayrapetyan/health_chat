package com.dinno.health_chat.api

import com.dinno.health_chat.model.HealthChatIntent
import com.dinno.health_chat.model.HealthChatState
import kotlinx.coroutines.flow.Flow

interface HealthChatManager {
    fun getChatState(): Flow<HealthChatState>
    suspend fun handleIntent(intent: HealthChatIntent)
}
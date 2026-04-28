package com.synq.app.core.network.websocket

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TypingManager @Inject constructor() {
    // Map of ChatId to (Map of UserId to isTyping)
    private val _typingStates = MutableStateFlow<Map<String, Map<String, Boolean>>>(emptyMap())

    fun getTypingState(chatId: String): kotlinx.coroutines.flow.Flow<Map<String, Boolean>> {
        return _typingStates.map { it[chatId] ?: emptyMap() }
    }

    fun setTyping(chatId: String, userId: String, isTyping: Boolean) {
        _typingStates.update { currentStates ->
            val chatState = currentStates[chatId]?.toMutableMap() ?: mutableMapOf()
            if (isTyping) {
                chatState[userId] = true
            } else {
                chatState.remove(userId)
            }
            currentStates + (chatId to chatState)
        }
    }
}

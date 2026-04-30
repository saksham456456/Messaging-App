package com.synq.app.core.network.websocket

import com.squareup.moshi.Moshi
import com.synq.app.core.util.SynqLog
import com.synq.app.core.network.TokenManager
import com.synq.app.data.local.dao.ChatDao
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.remote.dto.WsPayload
import com.synq.app.data.mapper.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val tokenManager: TokenManager,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao, // Inject ChatDao to bump chats
    private val typingManager: TypingManager
) {

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var reconnectJob: Job? = null
    private var isIntentionallyDisconnected = false
    private val payloadAdapter = moshi.adapter(WsPayload::class.java)

    fun connect() {
        if (webSocket != null) return
        isIntentionallyDisconnected = false

        val token = tokenManager.getToken() ?: return
        val request = Request.Builder()
            .url("wss://api.synq.app/ws")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                SynqLog.d("WebSocket", "Connected")
                reconnectJob?.cancel()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleIncomingMessage(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                SynqLog.d("WebSocket", "Closed: $reason")
                this@WebSocketManager.webSocket = null
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                SynqLog.e("WebSocket", "Failure: ${t.message}")
                this@WebSocketManager.webSocket = null
                scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        if (isIntentionallyDisconnected) return
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            SynqLog.d("WebSocket", "Attempting reconnect in 5 seconds...")
            delay(5000)
            connect()
        }
    }

    fun disconnect() {
        isIntentionallyDisconnected = true
        reconnectJob?.cancel()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    private fun handleIncomingMessage(text: String) {
        scope.launch {
            try {
                val payload = payloadAdapter.fromJson(text) ?: return@launch

                when (payload) {
                    is WsPayload.NewMessage -> {
                        val it = payload.message
                        messageDao.insertMessage(it.toEntity(isPending = false))
                        // IMPORTANT: Update the chat's updatedAt so it jumps to top of Chat List
                        chatDao.updateChatTimestamp(it.chatId, it.createdAt)
                    }
                    is WsPayload.Typing -> {
                        typingManager.setTyping(payload.chatId, payload.userId, payload.isTyping)
                    }
                }
            } catch (e: Exception) {
                SynqLog.e("WebSocket", "Failed to parse message payload", e)
            }
        }
    }
}

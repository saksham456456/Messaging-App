package com.synq.app.core.network.websocket

import com.squareup.moshi.Moshi
import com.synq.app.core.network.TokenManager
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.mapper.toEntity
import com.synq.app.data.remote.dto.WsBasePayload
import com.synq.app.data.remote.dto.WsMessagePayload
import com.synq.app.data.remote.dto.WsTypingPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val typingManager: TypingManager
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        if (webSocket != null) return
        val token = tokenManager.getToken() ?: return
        val request = Request.Builder().url("wss://api.synq.app/ws?token=$token").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingMessage(text)
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                this@WebSocketManager.webSocket = null
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                this@WebSocketManager.webSocket = null
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    private fun handleIncomingMessage(text: String) {
        scope.launch {
            try {
                val baseAdapter = moshi.adapter(WsBasePayload::class.java)
                val base = baseAdapter.fromJson(text) ?: return@launch

                when (base.type) {
                    "NEW_MESSAGE" -> {
                        val msgPayload = moshi.adapter(WsMessagePayload::class.java).fromJson(text)
                        msgPayload?.message?.let {
                            messageDao.insertMessage(it.toEntity(isPending = false))
                        }
                    }
                    "TYPING" -> {
                        val typingPayload = moshi.adapter(WsTypingPayload::class.java).fromJson(text)
                        typingPayload?.let {
                            typingManager.setTyping(it.chatId, it.userId, it.isTyping)
                        }
                    }
                    // DELIVERED / READ events can be handled here similarly by updating Room DB
                }
            } catch (e: Exception) {
                // Ignore parse errors from unknown payload types
            }
        }
    }
}

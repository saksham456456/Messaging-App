package com.synq.app.core.network.websocket

import android.util.Log
import com.squareup.moshi.Moshi
import com.synq.app.core.network.TokenManager
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.remote.dto.MessageDto
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
    private val messageDao: MessageDao
) {

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var reconnectJob: Job? = null
    private var isIntentionallyDisconnected = false

    fun connect() {
        if (webSocket != null) return
        isIntentionallyDisconnected = false

        val token = tokenManager.getToken() ?: return
        val request = Request.Builder().url("wss://api.synq.app/ws?token=$token").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d("WebSocket", "Connected")
                reconnectJob?.cancel() // Cancel any pending reconnects
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleIncomingMessage(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d("WebSocket", "Closed: $reason")
                this@WebSocketManager.webSocket = null
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e("WebSocket", "Failure: ${t.message}")
                this@WebSocketManager.webSocket = null
                scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        if (isIntentionallyDisconnected) return

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            Log.d("WebSocket", "Attempting reconnect in 5 seconds...")
            delay(5000) // Simple 5 second delay. A real app would use exponential backoff.
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
                val adapter = moshi.adapter(MessageDto::class.java)
                val messageDto = adapter.fromJson(text)
                if (messageDto != null) {
                    messageDao.insertMessage(messageDto.toEntity(isPending = false))
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Failed to parse message", e)
            }
        }
    }
}

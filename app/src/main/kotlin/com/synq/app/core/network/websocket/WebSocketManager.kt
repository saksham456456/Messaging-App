package com.synq.app.core.network.websocket
import com.squareup.moshi.Moshi
import com.synq.app.core.network.TokenManager
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.remote.dto.MessageDto
import com.synq.app.data.mapper.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class WebSocketManager @Inject constructor(private val client: OkHttpClient, private val moshi: Moshi, private val tokenManager: TokenManager, private val messageDao: MessageDao) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    fun connect() {
        if (webSocket != null) return
        val token = tokenManager.getToken() ?: return
        val request = Request.Builder().url("wss://api.synq.app/ws?token=$token").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { try { val msg = moshi.adapter(MessageDto::class.java).fromJson(text); if (msg != null) messageDao.insertMessage(msg.toEntity(isPending = false)) } catch (e: Exception) {} }
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) { this@WebSocketManager.webSocket = null }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { this@WebSocketManager.webSocket = null }
        })
    }
    fun disconnect() { webSocket?.close(1000, "User disconnected"); webSocket = null }
}

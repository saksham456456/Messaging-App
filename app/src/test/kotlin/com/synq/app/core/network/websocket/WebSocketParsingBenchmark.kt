package com.synq.app.core.network.websocket

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.synq.app.data.remote.dto.WsBasePayload
import com.synq.app.data.remote.dto.WsPayload
import org.junit.Test
import kotlin.system.measureNanoTime

class WebSocketParsingBenchmark {

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(WsPayload::class.java, "type")
                .withSubtype(WsPayload.NewMessage::class.java, "NEW_MESSAGE")
                .withSubtype(WsPayload.Typing::class.java, "TYPING")
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    private val messageJson = """
        {
            "type": "NEW_MESSAGE",
            "message": {
                "id": "msg_123",
                "chatId": "chat_456",
                "senderId": "user_789",
                "content": "Hello performance!",
                "type": "TEXT",
                "status": "SENT",
                "createdAt": 1710000000000
            }
        }
    """.trimIndent()

    private val typingJson = """
        {
            "type": "TYPING",
            "chatId": "chat_456",
            "userId": "user_789",
            "isTyping": true
        }
    """.trimIndent()

    @Test
    fun benchmarkOptimizedParsing() {
        val iterations = 1000

        // Warmup
        repeat(100) {
            handleIncomingMessageOptimized(messageJson)
            handleIncomingMessageOptimized(typingJson)
        }

        val time = measureNanoTime {
            repeat(iterations) {
                handleIncomingMessageOptimized(messageJson)
                handleIncomingMessageOptimized(typingJson)
            }
        }

        println("Average time for optimized parsing: ${time / (iterations * 2)} ns")
    }

    private fun handleIncomingMessageOptimized(text: String) {
        val payload = moshi.adapter(WsPayload::class.java).fromJson(text) ?: return

        when (payload) {
            is WsPayload.NewMessage -> {
                val it = payload.message
                // Simulated work
                it.id
            }
            is WsPayload.Typing -> {
                // Simulated work
                payload.chatId
            }
        }
    }
}

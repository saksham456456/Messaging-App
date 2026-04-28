package com.synq.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsBasePayload(
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class WsMessagePayload(
    @Json(name = "type") val type: String,
    @Json(name = "message") val message: MessageDto
)

@JsonClass(generateAdapter = true)
data class WsTypingPayload(
    @Json(name = "type") val type: String,
    @Json(name = "chatId") val chatId: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "isTyping") val isTyping: Boolean
)

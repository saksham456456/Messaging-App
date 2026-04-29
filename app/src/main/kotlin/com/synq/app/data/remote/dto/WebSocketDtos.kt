package com.synq.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class WsBasePayload(
    @Json(name = "type") open val type: String
)

sealed class WsPayload : WsBasePayload("") {
    @JsonClass(generateAdapter = true)
    data class NewMessage(
        @Json(name = "message") val message: MessageDto
    ) : WsPayload() {
        override val type: String = "NEW_MESSAGE"
    }

    @JsonClass(generateAdapter = true)
    data class Typing(
        @Json(name = "chatId") val chatId: String,
        @Json(name = "userId") val userId: String,
        @Json(name = "isTyping") val isTyping: Boolean
    ) : WsPayload() {
        override val type: String = "TYPING"
    }
}

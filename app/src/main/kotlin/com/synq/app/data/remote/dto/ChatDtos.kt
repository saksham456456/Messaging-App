package com.synq.app.data.remote.dto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true) data class ChatDto(@Json(name = "id") val id: String, @Json(name = "name") val name: String, @Json(name = "avatarUrl") val avatarUrl: String?, @Json(name = "lastMessage") val lastMessage: MessageDto?, @Json(name = "unreadCount") val unreadCount: Int, @Json(name = "updatedAt") val updatedAt: Long)
@JsonClass(generateAdapter = true) data class MessageDto(@Json(name = "id") val id: String, @Json(name = "chatId") val chatId: String, @Json(name = "senderId") val senderId: String, @Json(name = "content") val content: String, @Json(name = "type") val type: String, @Json(name = "status") val status: String, @Json(name = "createdAt") val createdAt: Long)
@JsonClass(generateAdapter = true) data class SendMessageRequestDto(@Json(name = "content") val content: String, @Json(name = "type") val type: String)

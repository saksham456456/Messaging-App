package com.synq.app.domain.model

data class Chat(val id: String, val name: String, val avatarUrl: String?, val unreadCount: Int, val lastMessage: Message?, val updatedAt: Long)
data class Message(val id: String, val chatId: String, val senderId: String, val content: String, val type: MessageType, val status: MessageStatus, val createdAt: Long, val isPending: Boolean = false)
enum class MessageType { TEXT, IMAGE, VIDEO, UNKNOWN }
enum class MessageStatus { PENDING, SENT, DELIVERED, READ, FAILED, UNKNOWN }

package com.synq.app.data.mapper

import com.synq.app.data.local.entity.ChatEntity
import com.synq.app.data.local.entity.MessageEntity
import com.synq.app.data.remote.dto.ChatDto
import com.synq.app.data.remote.dto.MessageDto
import com.synq.app.domain.model.Chat
import com.synq.app.domain.model.Message
import com.synq.app.domain.model.MessageStatus
import com.synq.app.domain.model.MessageType

fun ChatDto.toEntity() = ChatEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    unreadCount = unreadCount,
    updatedAt = updatedAt
)

fun MessageDto.toEntity(isPending: Boolean = false) = MessageEntity(
    id = id,
    chatId = chatId,
    senderId = senderId,
    content = content,
    type = type,
    status = status,
    createdAt = createdAt,
    isPending = isPending
)

fun ChatEntity.toDomain(lastMessage: Message? = null) = Chat(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    unreadCount = unreadCount,
    lastMessage = lastMessage,
    updatedAt = updatedAt
)

fun MessageEntity.toDomain() = Message(
    id = id,
    chatId = chatId,
    senderId = senderId,
    content = content,
    type = type.toMessageType(),
    status = status.toMessageStatus(),
    createdAt = createdAt,
    isPending = isPending
)

private fun String.toMessageType(): MessageType = try {
    MessageType.valueOf(this)
} catch (e: Exception) {
    MessageType.UNKNOWN
}

private fun String.toMessageStatus(): MessageStatus = try {
    MessageStatus.valueOf(this)
} catch (e: Exception) {
    MessageStatus.UNKNOWN
}

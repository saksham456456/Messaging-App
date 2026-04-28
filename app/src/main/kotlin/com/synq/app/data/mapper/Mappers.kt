package com.synq.app.data.mapper
import com.synq.app.data.local.entity.ChatEntity
import com.synq.app.data.local.entity.MessageEntity
import com.synq.app.data.remote.dto.ChatDto
import com.synq.app.data.remote.dto.MessageDto
import com.synq.app.domain.model.Chat
import com.synq.app.domain.model.Message
import com.synq.app.domain.model.MessageStatus
import com.synq.app.domain.model.MessageType

fun ChatDto.toEntity() = ChatEntity(id, name, avatarUrl, unreadCount, updatedAt)
fun MessageDto.toEntity(isPending: Boolean = false) = MessageEntity(id, chatId, senderId, content, type, status, createdAt, isPending)
fun ChatEntity.toDomain(lastMessage: Message? = null) = Chat(id, name, avatarUrl, unreadCount, lastMessage, updatedAt)
fun MessageEntity.toDomain() = Message(id, chatId, senderId, content, try { MessageType.valueOf(type) } catch (e: Exception) { MessageType.UNKNOWN }, try { MessageStatus.valueOf(status) } catch (e: Exception) { MessageStatus.UNKNOWN }, createdAt, isPending)

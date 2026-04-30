package com.synq.app.data.mapper

import com.synq.app.data.local.entity.MessageEntity
import com.synq.app.domain.model.MessageStatus
import com.synq.app.domain.model.MessageType
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun `MessageEntity toDomain with valid status and type maps correctly`() {
        val entity = MessageEntity(
            id = "1",
            chatId = "chat_1",
            senderId = "sender_1",
            content = "Hello",
            type = "TEXT",
            status = "DELIVERED",
            createdAt = 1000L,
            isPending = false
        )

        val domainModel = entity.toDomain()

        assertEquals("1", domainModel.id)
        assertEquals("chat_1", domainModel.chatId)
        assertEquals("sender_1", domainModel.senderId)
        assertEquals("Hello", domainModel.content)
        assertEquals(MessageType.TEXT, domainModel.type)
        assertEquals(MessageStatus.DELIVERED, domainModel.status)
        assertEquals(1000L, domainModel.createdAt)
        assertEquals(false, domainModel.isPending)
    }

    @Test
    fun `MessageEntity toDomain with invalid status and type falls back to UNKNOWN`() {
        val entity = MessageEntity(
            id = "2",
            chatId = "chat_1",
            senderId = "sender_1",
            content = "Invalid message",
            type = "INVALID_TYPE",
            status = "INVALID_STATUS",
            createdAt = 2000L,
            isPending = true
        )

        val domainModel = entity.toDomain()

        assertEquals(MessageType.UNKNOWN, domainModel.type)
        assertEquals(MessageStatus.UNKNOWN, domainModel.status)
    }
}

package com.synq.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
@Entity(tableName = "messages", indices = [Index("chatId"), Index("createdAt")]) data class MessageEntity(@PrimaryKey val id: String, val chatId: String, val senderId: String, val content: String, val type: String, val status: String, val createdAt: Long, val isPending: Boolean = false)

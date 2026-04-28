package com.synq.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "chats") data class ChatEntity(@PrimaryKey val id: String, val name: String, val avatarUrl: String?, val unreadCount: Int, val updatedAt: Long)

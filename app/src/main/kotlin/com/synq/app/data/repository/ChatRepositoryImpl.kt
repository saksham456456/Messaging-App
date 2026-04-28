package com.synq.app.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.synq.app.core.network.TokenManager
import com.synq.app.core.network.toUserFriendlyMessage
import com.synq.app.data.local.dao.ChatDao
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.local.entity.MessageEntity
import com.synq.app.data.mapper.toDomain
import com.synq.app.data.mapper.toEntity
import com.synq.app.data.remote.api.ChatApi
import com.synq.app.data.remote.dto.SendMessageRequestDto
import com.synq.app.data.remote.dto.CreateChatRequestDto
import com.synq.app.data.repository.mediator.MessageRemoteMediator
import com.synq.app.domain.model.Chat
import com.synq.app.domain.model.Message
import com.synq.app.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val tokenManager: TokenManager
) : ChatRepository {

    override fun getChats(): Flow<PagingData<Chat>> = Pager(PagingConfig(pageSize = 20)) { chatDao.getChats() }.flow.map { it.map { c -> c.toDomain() } }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMessages(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = false),
            remoteMediator = MessageRemoteMediator(chatId, chatApi, messageDao),
            pagingSourceFactory = { messageDao.getMessagesForChat(chatId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(chatId: String, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = tokenManager.getUserId() ?: "unknown"
        val tempMessageId = UUID.randomUUID().toString()

        val pendingMessage = MessageEntity(
            id = tempMessageId,
            chatId = chatId,
            senderId = currentUserId,
            content = content,
            type = "TEXT",
            status = "PENDING",
            createdAt = System.currentTimeMillis(),
            isPending = true
        )
        messageDao.insertMessage(pendingMessage)

        try {
            val response = chatApi.sendMessage(chatId, SendMessageRequestDto(content, "TEXT"))
            if (response.isSuccessful && response.body() != null) {
                messageDao.clearMessages(tempMessageId) // delete pending
                messageDao.insertMessage(response.body()!!.toEntity(isPending = false))
                Result.success(Unit)
            } else {
                messageDao.updateMessageStatus(tempMessageId, "FAILED")
                Result.failure(Exception("Failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            messageDao.updateMessageStatus(tempMessageId, "FAILED")
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun syncChats() = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getChats(1, 50)
            if (response.isSuccessful && response.body() != null) {
                val chats = response.body()!!
                chatDao.insertChats(chats.map { it.toEntity() })
                val msgs = chats.mapNotNull { it.lastMessage?.toEntity() }
                if (msgs.isNotEmpty()) messageDao.insertMessages(msgs)
            }
        } catch (e: Exception) {}
    }

    override suspend fun createChat(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.createChat(CreateChatRequestDto(phoneNumber))
            if (response.isSuccessful && response.body() != null) {
                val newChat = response.body()!!
                chatDao.insertChat(newChat.toEntity())
                Result.success(newChat.id)
            } else {
                Result.failure(Exception("Failed to start chat: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}

package com.synq.app.data.repository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.synq.app.data.local.dao.ChatDao
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.mapper.toDomain
import com.synq.app.data.mapper.toEntity
import com.synq.app.data.remote.api.ChatApi
import com.synq.app.data.remote.dto.SendMessageRequestDto
import com.synq.app.domain.model.Chat
import com.synq.app.domain.model.Message
import com.synq.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
class ChatRepositoryImpl @Inject constructor(private val chatApi: ChatApi, private val chatDao: ChatDao, private val messageDao: MessageDao) : ChatRepository {
    override fun getChats(): Flow<PagingData<Chat>> = Pager(PagingConfig(pageSize = 20)) { chatDao.getChats() }.flow.map { it.map { c -> c.toDomain() } }
    override fun getMessages(chatId: String): Flow<PagingData<Message>> = Pager(PagingConfig(pageSize = 50)) { messageDao.getMessagesForChat(chatId) }.flow.map { it.map { m -> m.toDomain() } }
    override suspend fun sendMessage(chatId: String, content: String): Result<Unit> {
        return try { val response = chatApi.sendMessage(chatId, SendMessageRequestDto(content, "TEXT")); if (response.isSuccessful && response.body() != null) { messageDao.insertMessage(response.body()!!.toEntity(isPending = false)); Result.success(Unit) } else Result.failure(Exception("Failed: ${response.message()}")) } catch (e: Exception) { Result.failure(e) }
    }
    override suspend fun syncChats() { try { val response = chatApi.getChats(1, 50); if (response.isSuccessful && response.body() != null) { val chats = response.body()!!; chatDao.insertChats(chats.map { it.toEntity() }); val msgs = chats.mapNotNull { it.lastMessage?.toEntity() }; if (msgs.isNotEmpty()) messageDao.insertMessages(msgs) } } catch (e: Exception) {} }
}

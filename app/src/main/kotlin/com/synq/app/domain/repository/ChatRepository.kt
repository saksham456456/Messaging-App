package com.synq.app.domain.repository
import androidx.paging.PagingData
import com.synq.app.domain.model.Chat
import com.synq.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<PagingData<Chat>>
    fun getMessages(chatId: String): Flow<PagingData<Message>>
    suspend fun sendMessage(chatId: String, content: String): Result<Unit>
    suspend fun syncChats()
}

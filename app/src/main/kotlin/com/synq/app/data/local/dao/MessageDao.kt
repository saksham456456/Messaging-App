package com.synq.app.data.local.dao
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.synq.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
@Dao interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt DESC") fun getMessagesForChat(chatId: String): PagingSource<Int, MessageEntity>
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt DESC LIMIT 1") fun getLastMessageForChat(chatId: String): Flow<MessageEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMessages(messages: List<MessageEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMessage(message: MessageEntity)
    @Query("UPDATE messages SET status = :status, isPending = 0 WHERE id = :messageId") suspend fun updateMessageStatus(messageId: String, status: String)
    @Query("DELETE FROM messages WHERE chatId = :chatId") suspend fun clearMessages(chatId: String)
}

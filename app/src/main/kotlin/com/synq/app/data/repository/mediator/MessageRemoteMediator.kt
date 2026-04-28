package com.synq.app.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.local.entity.MessageEntity
import com.synq.app.data.mapper.toEntity
import com.synq.app.data.remote.api.ChatApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val chatId: String,
    private val chatApi: ChatApi,
    private val messageDao: MessageDao
) : RemoteMediator<Int, MessageEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {
            val cursor: Long? = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // Fetch the oldest message we currently have to use as a cursor
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    lastItem.createdAt
                }
            }

            val response = chatApi.getMessages(
                chatId = chatId,
                cursor = cursor,
                limit = state.config.pageSize
            )

            if (response.isSuccessful && response.body() != null) {
                val messages = response.body()!!
                val endOfPaginationReached = messages.isEmpty()

                if (loadType == LoadType.REFRESH) {
                    // Note: We don't clear all messages here because of optimistic UI (pending messages).
                    // In a production app, we'd have a more sophisticated sync strategy.
                    // For now, we rely on OnConflictStrategy.REPLACE.
                }

                messageDao.insertMessages(messages.map { it.toEntity(isPending = false) })

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

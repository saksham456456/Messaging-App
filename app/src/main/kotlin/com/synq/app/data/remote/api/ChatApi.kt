package com.synq.app.data.remote.api
import com.synq.app.data.remote.dto.ChatDto
import com.synq.app.data.remote.dto.MessageDto
import com.synq.app.data.remote.dto.SendMessageRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface ChatApi {
    @GET("chats") suspend fun getChats(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<ChatDto>>
    @GET("chats/{chatId}/messages") suspend fun getMessages(@Path("chatId") chatId: String, @Query("cursor") cursor: Long?, @Query("limit") limit: Int): Response<List<MessageDto>>
    @POST("chats/{chatId}/messages") suspend fun sendMessage(@Path("chatId") chatId: String, @Body request: SendMessageRequestDto): Response<MessageDto>
}

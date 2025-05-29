package com.example.network.api

import com.example.network.api.dto.ChatDto
import com.example.network.api.dto.MessagePageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {
    @GET(".")
    suspend fun getChatList(): List<ChatDto>

    @GET("{chatId}")
    suspend fun getChatMessages(
        @Path("chatId") chatId: String,
        @Query("beforeMessageId") beforeMessageId: String?,
        @Query("limit") limit: Int
    ): MessagePageResponse
}

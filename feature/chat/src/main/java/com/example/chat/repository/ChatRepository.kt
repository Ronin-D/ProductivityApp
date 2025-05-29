package com.example.chat.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.network.api.ChatApi
import com.example.network.api.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatApi: ChatApi
) {
    fun getMessages(chatId: String): Flow<PagingData<MessageDto>> {
        return Pager(
            PagingConfig(pageSize = 50)
        ) {
            ChatPagingSource(chatId, chatApi)
        }.flow
    }
}
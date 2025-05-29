package com.example.chat.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.network.api.ChatApi
import com.example.network.api.dto.MessageDto
import javax.inject.Inject

class ChatPagingSource @Inject constructor(
    private val chatId: String,
    private val chatApi: ChatApi
) : PagingSource<String, MessageDto>() {

    override fun getRefreshKey(state: PagingState<String, MessageDto>): String? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MessageDto> {
        val beforeMessageId = params.key
        return try {
            val pageSize = params.loadSize
            val messages = chatApi.getChatMessages(
                chatId = chatId,
                beforeMessageId = beforeMessageId,
                limit = pageSize
            ).items

            val nextKey = if (messages.isEmpty()) {
                null
            } else {
                messages.last().id
            }

            LoadResult.Page(
                data = messages,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
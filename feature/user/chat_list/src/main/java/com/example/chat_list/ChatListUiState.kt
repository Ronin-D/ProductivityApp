package com.example.chat_list

import com.example.network.api.dto.ChatDto

sealed interface ChatListUiState {
    object Loading : ChatListUiState
    data class Success(val chats: List<ChatDto>) : ChatListUiState
    data class Error(val message: String) : ChatListUiState
}

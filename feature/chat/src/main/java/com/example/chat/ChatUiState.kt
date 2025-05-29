package com.example.chat

sealed interface ChatUiState {
    object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
    object Success : ChatUiState
}

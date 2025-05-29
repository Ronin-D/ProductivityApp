package com.example.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_store.AuthDataStoreRepository
import com.example.network.api.ChatApi
import com.example.network.api.dto.MessageDto
import com.example.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatApi: ChatApi,
    private val webSocketManager: WebSocketManager,
    private val authDataStore: AuthDataStoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId = savedStateHandle.get<String>("chatId") ?: error("chatId is required")

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState

    private val _messages = MutableStateFlow<List<MessageDto>>(emptyList())
    val messages: StateFlow<List<MessageDto>> = _messages

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput

    init {
        connectWebSocket()
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                val messages = chatApi.getChatMessages(chatId, null, 100)
                _messages.update { it + messages.items }
                ChatUiState.Success
            } catch (e: Exception) {
                ChatUiState.Error("Ошибка при загрузке сообщений: ${e.localizedMessage}")
            }
        }
    }

    private fun connectWebSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = authDataStore.getAccessToken().first()
            webSocketManager.connect(token!!, chatId)

            webSocketManager.incomingMessages.collect { message ->
                Log.d("WebSocket", "received message $message")
                _messages.update { current ->
                    val updated = current.filterNot {
                        it.isOwner && it.content == message.content && it.id.startsWith("temp-")
                    }
                    updated + message
                }
            }

        }
    }

    fun onMessageInputChange(newInput: String) {
        _messageInput.value = newInput
    }

    fun sendMessage() {
        val content = _messageInput.value.trim()
        if (content.isNotEmpty()) {
            val tempMessage = MessageDto(
                id = "temp-${UUID.randomUUID()}",
                isOwner = true,
                content = content,
                timestamp = ZonedDateTime.now().toString(),
                isSending = true
            )
            _messages.update { it + tempMessage }
            _messageInput.value = ""
            webSocketManager.sendMessage(content)
        }
    }


    fun retry() {
        _uiState.value = ChatUiState.Loading
        loadMessages()
    }

    override fun onCleared() {
        webSocketManager.close()
        super.onCleared()
    }
}

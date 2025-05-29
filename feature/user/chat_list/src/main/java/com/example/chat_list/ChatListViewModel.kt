package com.example.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.api.ChatApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatApi: ChatApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _uiState.value = try {
                val chats = chatApi.getChatList()
                ChatListUiState.Success(chats)
            } catch (e: Exception) {
                ChatListUiState.Error("Не удалось загрузить чаты: ${e.localizedMessage}")
            }
        }
    }

    fun retry() {
        _uiState.value = ChatListUiState.Loading
        loadChats()
    }
}



package com.example.chat_list

import Error
import Loading
import ScreenLabel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.api.dto.ChatDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatListRoute(
    viewModel: ChatListViewModel = hiltViewModel(),
    onChatSelected: (chatId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ChatListScreen(
        state = state,
        onRetry = viewModel::retry,
        onChatSelected = onChatSelected,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun ChatListScreen(
    state: ChatListUiState,
    onRetry: () -> Unit,
    onChatSelected: (chatId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (state) {
            ChatListUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

            is ChatListUiState.Error -> Error(
                cause = state.message,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )

            is ChatListUiState.Success -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ScreenLabel(title = "Чаты", modifier = Modifier.fillMaxWidth())
                }

                items(state.chats) { chat ->
                    ChatListItem(chat = chat, onClick = { onChatSelected(chat.chatId) })
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatDto, onClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault())
    val lastUpdateTimestamp = ZonedDateTime.parse(chat.lastMessageTimestamp)
        .withZoneSameInstant(ZoneId.systemDefault())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)) {
        Text(text = chat.participantName, style = MaterialTheme.typography.titleMedium)
        Text(text = chat.participantEmail, style = MaterialTheme.typography.titleMedium)
        Text(text = chat.lastMessage, color = Color.Gray)
        Text(
            text = lastUpdateTimestamp.format(formatter),
            color = Color.LightGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

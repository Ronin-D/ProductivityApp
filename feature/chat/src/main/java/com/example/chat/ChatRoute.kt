package com.example.chat

import Error
import Loading
import ScreenLabel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.api.dto.MessageDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatRoute(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()
    val messages by viewModel.messages.collectAsState()

    ChatScreen(
        state = state,
        messages = messages,
        input = messageInput,
        onInputChange = viewModel::onMessageInputChange,
        onSend = viewModel::sendMessage,
        onRetry = viewModel::retry,
        onNavigateBack = onNavigateBack,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun ChatScreen(
    state: ChatUiState,
    messages: List<MessageDto>,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenLabel(title = "Чат", onNavigateBack = onNavigateBack)

        when (state) {
            ChatUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

            is ChatUiState.Error -> Error(
                cause = state.message,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )

            is ChatUiState.Success -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(
                        messages.reversed()
                        //key = { it.id } TODO: Uncomment when backend will be ready
                    ) { message ->
                        MessageItem(message)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color(0xFFFFA061)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = input,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFFFA061),
                                focusedContainerColor = Color(0xFFFFA061),
                                focusedIndicatorColor = Color(0xFFFFA061),
                                unfocusedIndicatorColor = Color(0xFFFFA061)
                            ),
                            onValueChange = onInputChange,
                            modifier = Modifier.weight(4f),
                            placeholder = { Text("Сообщение") }
                        )
                        IconButton(onClick = onSend, modifier = Modifier.weight(1f)) {
                            Icon(
                                contentDescription = "send message button",
                                imageVector = Icons.Default.Send
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun MessageItem(message: MessageDto) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault())
    val lastUpdateTimestamp = ZonedDateTime.parse(message.timestamp)
        .withZoneSameInstant(ZoneId.systemDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (message.isOwner) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (message.isOwner) Color(0xFFD0F0C0) else Color(0xFFE0E0E0)
                )
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = message.content)

                if (message.isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(start = 8.dp),
                        strokeWidth = 2.dp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = lastUpdateTimestamp.format(formatter),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

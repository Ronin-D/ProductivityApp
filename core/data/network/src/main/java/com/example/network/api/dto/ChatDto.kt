package com.example.network.api.dto

data class ChatDto(
    val chatId: String,
    val participantName: String,
    val participantEmail: String,
    val lastMessage: String,
    val lastMessageTimestamp: String
)
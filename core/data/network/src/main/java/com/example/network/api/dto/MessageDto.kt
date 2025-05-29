package com.example.network.api.dto

data class MessageDto(
    val id: String,
    val isOwner: Boolean,
    val content: String,
    val timestamp: String,
    val isSending: Boolean = false
)

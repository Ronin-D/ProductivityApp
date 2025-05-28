package com.example.network.api.dto

data class UsageStatDto(
    val packageName: String,
    val appName: String,
    val foregroundTimeMillis: Long
)

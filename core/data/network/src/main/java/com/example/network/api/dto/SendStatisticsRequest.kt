package com.example.network.api.dto

data class SendStatisticsRequest(
    val date: String,
    val stats: List<UsageStatDto>
)


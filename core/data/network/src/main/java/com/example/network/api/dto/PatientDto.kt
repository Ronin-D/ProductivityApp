package com.example.network.api.dto

data class PatientDto(
    val id: String,
    val chatId: String,
    val statisticsId: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String
)

package com.example.network.api.dto

data class PatientStatisticsResponse(
    val patient: PatientDto,
    val statistics: List<AppStatisticsDto>
)
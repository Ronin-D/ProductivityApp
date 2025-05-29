package com.example.patient_statistics

import com.example.network.api.dto.PatientStatisticsResponse

sealed interface PatientStatisticsUiState {
    object Loading : PatientStatisticsUiState
    data class Success(val response: PatientStatisticsResponse) : PatientStatisticsUiState
    data class Error(val message: String) : PatientStatisticsUiState
}

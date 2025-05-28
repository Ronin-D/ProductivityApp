package com.example.profile

sealed interface SendStatisticsUiState {
    object Idle : SendStatisticsUiState
    object Loading : SendStatisticsUiState
    object Success : SendStatisticsUiState
    data class Error(val message: String) : SendStatisticsUiState
}

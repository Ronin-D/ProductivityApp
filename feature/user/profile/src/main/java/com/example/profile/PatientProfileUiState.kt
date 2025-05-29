package com.example.profile

import com.example.network.api.dto.UserDto

sealed interface PatientProfileUiState {

    object Loading : PatientProfileUiState

    data class Error(
        val message: String
    ) : PatientProfileUiState

    data class Success(
        val userDto: UserDto
    ) : PatientProfileUiState
}
package com.example.profile

import com.example.network.api.dto.DoctorProfileDto

sealed interface DoctorProfileUiState {

    object Loading : DoctorProfileUiState

    data class Error(
        val message: String
    ) : DoctorProfileUiState

    data class Success(
        val doctorProfileDto: DoctorProfileDto
    ) : DoctorProfileUiState
}
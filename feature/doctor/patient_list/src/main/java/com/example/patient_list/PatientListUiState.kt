package com.example.patient_list

import com.example.network.api.dto.PatientDto

sealed interface PatientListUiState {
    object Loading : PatientListUiState
    data class Success(val patients: List<PatientDto>) : PatientListUiState
    data class Error(val message: String) : PatientListUiState
}

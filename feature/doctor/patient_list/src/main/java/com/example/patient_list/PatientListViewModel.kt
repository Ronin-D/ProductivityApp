package com.example.patient_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.api.PatientApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListViewModel @Inject constructor(
    private val patientApi: PatientApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientListUiState>(PatientListUiState.Loading)
    val uiState: StateFlow<PatientListUiState> = _uiState

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _uiState.value = try {
                val patients = patientApi.getPatients()
                PatientListUiState.Success(patients)
            } catch (e: Exception) {
                PatientListUiState.Error("Не удалось загрузить пациентов: ${e.localizedMessage}")
            }
        }
    }

    fun retry() {
        _uiState.value = PatientListUiState.Loading
        loadChats()
    }
}



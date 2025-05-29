package com.example.patient_statistics

import TimeRange
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.api.PatientApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class PatientStatisticsViewModel @Inject constructor(
    private val patientApi: PatientApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PatientStatisticsUiState>(PatientStatisticsUiState.Loading)
    val uiState: StateFlow<PatientStatisticsUiState> = _uiState

    private val _selectedRange = MutableStateFlow(TimeRange.LAST_7_DAYS)
    val selectedRange: StateFlow<TimeRange> = _selectedRange

    private val _customStart = MutableStateFlow<ZonedDateTime?>(null)
    val customStart: StateFlow<ZonedDateTime?> = _customStart

    private val _customEnd = MutableStateFlow<ZonedDateTime?>(null)
    val customEnd: StateFlow<ZonedDateTime?> = _customEnd

    private val patientId =
        savedStateHandle.get<String>("patientId") ?: error("patientId is required")

    init {
        loadStats()
    }

    fun retry() {
        _uiState.value = PatientStatisticsUiState.Loading
        loadStats()
    }

    fun setCustomDates(start: ZonedDateTime, end: ZonedDateTime) {
        _customStart.value = start
        _customEnd.value = end
        _selectedRange.value = TimeRange.CUSTOM
        loadStats()
    }

    fun onTimeRangeSelected(range: TimeRange) {
        _selectedRange.value = range
        loadStats()
    }

    private fun loadStats() {
        _uiState.value = PatientStatisticsUiState.Loading
        viewModelScope.launch {
            try {
                val now = ZonedDateTime.now()

                val (start, end) = when (_selectedRange.value) {
                    TimeRange.CUSTOM -> {
                        val start = _customStart.value ?: now.minusDays(7)
                        val end = _customEnd.value ?: now
                        start to end
                    }

                    else -> {
                        val days = _selectedRange.value.days!!.toLong()
                        now.minusDays(days) to now
                    }
                }

                val result = patientApi.getPatientStatistics(patientId, start, end)
                _uiState.value = PatientStatisticsUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value =
                    PatientStatisticsUiState.Error("Ошибка загрузки: ${e.localizedMessage}")
            }
        }
    }
}

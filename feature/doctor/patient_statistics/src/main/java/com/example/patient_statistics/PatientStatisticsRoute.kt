package com.example.patient_statistics

import Error
import Loading
import ScreenLabel
import TimeRange
import TimeRangeDropdown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.api.dto.AppStatisticsDto
import showCustomDatePicker
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PatientStatisticsRoute(
    viewModel: PatientStatisticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()
    val customStart by viewModel.customStart.collectAsState()
    val customEnd by viewModel.customEnd.collectAsState()

    PatientStatisticsScreen(
        state = state,
        selectedRange = selectedRange,
        customStart = customStart,
        customEnd = customEnd,
        onTimeRangeSelected = viewModel::onTimeRangeSelected,
        onCustomDateSelected = viewModel::setCustomDates,
        onRetry = viewModel::retry,
        onNavigateBack = onNavigateBack
    )
}


@Composable
fun PatientStatisticsScreen(
    state: PatientStatisticsUiState,
    selectedRange: TimeRange,
    customStart: ZonedDateTime?,
    customEnd: ZonedDateTime?,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onCustomDateSelected: (ZonedDateTime, ZonedDateTime) -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val showPicker = remember { mutableStateOf(false) }

    if (showPicker.value) {
        showCustomDatePicker(
            onDatesSelected = { start, end ->
                onCustomDateSelected(start, end)
                showPicker.value = false
            },
            onPickerDismissed = {
                showPicker.value = false
            }
        )
    }

    Box(modifier = modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            when (state) {
                PatientStatisticsUiState.Loading -> {
                    item {
                        ScreenLabel(
                            title = "Загрузка",
                            modifier = Modifier.fillMaxWidth(),
                            onNavigateBack = onNavigateBack
                        )
                    }
                    item { Loading(modifier = Modifier.fillMaxSize()) }
                }

                is PatientStatisticsUiState.Error -> {
                    item {
                        ScreenLabel(
                            title = "Ошибка",
                            modifier = Modifier.fillMaxWidth(),
                            onNavigateBack = onNavigateBack
                        )
                    }
                    item {
                        Error(
                            cause = state.message,
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is PatientStatisticsUiState.Success -> {
                    val patient = state.response.patient
                    val apps = state.response.statistics

                    item {
                        ScreenLabel(
                            title = "${patient.surname} ${patient.name} ${patient.patronymic}",
                            modifier = Modifier.fillMaxWidth(),
                            onNavigateBack = onNavigateBack
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            TimeRangeDropdown(
                                selected = selectedRange,
                                onRangeSelected = onTimeRangeSelected,
                                onRequestCustom = { showPicker.value = true }
                            )

                            if (selectedRange == TimeRange.CUSTOM && customStart != null && customEnd != null) {
                                val formatter = DateTimeFormatter.ofPattern(
                                    "dd.MM.yyyy HH:mm",
                                    Locale.getDefault()
                                )
                                val startDate = formatter.format(customStart)
                                val endDate = formatter.format(customEnd)
                                Text(
                                    text = "С $startDate по $endDate",
                                    modifier = Modifier.padding(top = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    items(apps.size) { index ->
                        AppItem(apps[index], index + 1)
                    }
                }
            }

        }
    }
}


@Composable
fun AppItem(app: AppStatisticsDto, number: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${number}.")
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = app.appName)
            Text(text = "Экранное время: ${app.totalTime} мин")
        }
    }
}
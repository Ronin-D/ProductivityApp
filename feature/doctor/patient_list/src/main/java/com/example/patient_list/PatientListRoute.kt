package com.example.patient_list

import Error
import Loading
import ScreenLabel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.api.dto.PatientDto

@Composable
fun PatientListRoute(
    viewModel: PatientListViewModel = hiltViewModel(),
    onChatSelected: (chatId: String) -> Unit,
    onPatientStatisticsSelected: (patientId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    PatientListScreen(
        state = state,
        onRetry = viewModel::retry,
        onChatSelected = onChatSelected,
        onPatientStatisticsSelected = onPatientStatisticsSelected,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun PatientListScreen(
    state: PatientListUiState,
    onRetry: () -> Unit,
    onChatSelected: (chatId: String) -> Unit,
    onPatientStatisticsSelected: (patientId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (state) {
            PatientListUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

            is PatientListUiState.Error -> Error(
                cause = state.message,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )

            is PatientListUiState.Success -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ScreenLabel(title = "Пациенты", modifier = Modifier.fillMaxWidth())
                }

                items(state.patients) { patient ->
                    PatientListItem(
                        patient = patient,
                        onPatientStatisticsSelected = { onPatientStatisticsSelected(patient.id) },
                        onChatSelected = { onChatSelected(patient.chatId) })
                }
            }
        }
    }
}

@Composable
fun PatientListItem(
    patient: PatientDto,
    onChatSelected: () -> Unit,
    onPatientStatisticsSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${patient.surname} ${patient.name} ${patient.patronymic}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onPatientStatisticsSelected) {
            Icon(
                contentDescription = "patient statistics button",
                imageVector = ImageVector.vectorResource(R.drawable.statistics)
            )
        }
        IconButton(onClick = onChatSelected) {
            Icon(
                contentDescription = "chat button",
                imageVector = Icons.Default.MailOutline
            )
        }
    }
}


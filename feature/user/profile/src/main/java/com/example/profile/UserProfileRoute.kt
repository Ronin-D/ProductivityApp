package com.example.profile

import Error
import Loading
import ScreenLabel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.api.dto.UserDto

@Composable
fun UserProfileRoute(
    viewModel: PatientProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val sendState by viewModel.sendStatisticsUiState.collectAsState()

    ProfileScreen(
        state = state,
        sendState = sendState,
        onRetry = {
            viewModel.retry()
        },
        onPublishStatistics = {
            viewModel.sendStatistics(context)
        },
        onSignOut = {
            viewModel.logout {
                onSignOut()
            }
        },
        onSendStatisticsStateReset = {
            viewModel.resetSendStatisticsState()
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
internal fun ProfileScreen(
    state: PatientProfileUiState,
    sendState: SendStatisticsUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    onPublishStatistics: () -> Unit,
    onSendStatisticsStateReset: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        when (state) {
            PatientProfileUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

            is PatientProfileUiState.Success -> Content(
                userDto = state.userDto,
                modifier = modifier,
                onPublishStatistics = onPublishStatistics,
                onSignOut = onSignOut
            )

            is PatientProfileUiState.Error -> Error(
                cause = "Не удалось загрузить данные профиля",
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
        }

        when (sendState) {
            SendStatisticsUiState.Idle -> {}

            SendStatisticsUiState.Loading -> Loading(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )

            is SendStatisticsUiState.Success -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Статистика успешно отправлена", Toast.LENGTH_SHORT)
                        .show()
                    onSendStatisticsStateReset()
                }
            }

            is SendStatisticsUiState.Error -> {
                LaunchedEffect(sendState.message) {
                    Toast.makeText(context, sendState.message, Toast.LENGTH_SHORT).show()
                    onSendStatisticsStateReset()
                }
            }
        }
    }
}


@Composable
internal fun Content(
    modifier: Modifier = Modifier,
    userDto: UserDto,
    onSignOut: () -> Unit,
    onPublishStatistics: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScreenLabel(title = "Профиль", modifier = Modifier.fillMaxWidth())
        }

        item {
            ProfileItem(
                label = "ФИО",
                value = "${userDto.surname} ${userDto.name} ${userDto.patronymic}",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Person
            )
        }

        item {
            ProfileItem(
                label = "Роль",
                value = "Пациент",
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(R.drawable.role_icon)
            )
        }

        item {
            ProfileItem(
                label = "Электронная почта ",
                value = "${userDto.email}",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Email
            )
        }

        items(userDto.doctors) { doctor ->
            ProfileItem(
                label = "ФИО психотерапевта",
                value = "${doctor.surname} ${doctor.name} ${doctor.patronymic}",
                icon = Icons.Outlined.AccountCircle
            )
        }

        item {
            Button(
                onClick = onPublishStatistics,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Опубликовать статистику за сегодня")
            }
        }

        item {
            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Выйти")
            }
        }
    }
}

@Composable
internal fun ProfileItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    value: String
) {
    Box(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = "icon", modifier = Modifier.size(30.dp))
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = label, color = Color.Gray)
                Text(text = value)
            }
        }
    }
}
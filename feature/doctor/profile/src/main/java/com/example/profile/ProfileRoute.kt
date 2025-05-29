package com.example.profile

import Error
import Loading
import ScreenLabel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.network.api.dto.DoctorProfileDto

@Composable
fun DoctorProfileRoute(
    viewModel: DoctorProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ProfileScreen(
        state = state,
        onRetry = {
            viewModel.retry()
        },
        onSignOut = {
            viewModel.logout {
                onSignOut()
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
internal fun ProfileScreen(
    state: DoctorProfileUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        when (state) {
            DoctorProfileUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

            is DoctorProfileUiState.Success -> Content(
                doctorProfileDto = state.doctorProfileDto,
                modifier = modifier,
                onSignOut = onSignOut
            )

            is DoctorProfileUiState.Error -> Error(
                cause = "Не удалось загрузить данные профиля",
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
internal fun Content(
    modifier: Modifier = Modifier,
    doctorProfileDto: DoctorProfileDto,
    onSignOut: () -> Unit,
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
                value = "${doctorProfileDto.surname} ${doctorProfileDto.name} ${doctorProfileDto.patronymic}",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Person
            )
        }

        item {
            ProfileItem(
                label = "Роль",
                value = "Врач",
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(R.drawable.role_icon)
            )
        }

        item {
            ProfileItem(
                label = "Электронная почта ",
                value = "${doctorProfileDto.email}",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Email
            )
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
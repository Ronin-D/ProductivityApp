package com.example.app_statistics

import TimeRange
import TimeRangeDropdown
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app_statistics.model.UsageApp
import kotlinx.coroutines.delay
import showCustomDatePicker
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Composable
fun AppStatisticsRoute(
    viewModel: AppStatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.appStats.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()
    val customStart by viewModel.customStart.collectAsState()
    val customEnd by viewModel.customEnd.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermissionAndLoadData()
        while (!hasPermission) {
            delay(2000)
            viewModel.checkPermissionAndLoadData()
        }
    }

    AppStatisticsScreen(
        modifier = Modifier.fillMaxSize(),
        appStats = stats,
        hasPermission = hasPermission,
        selectedRange = selectedRange,
        customStart = customStart,
        customEnd = customEnd,
        onRequestPermission = viewModel::requestPermission,
        onTimeRangeSelected = viewModel::onTimeRangeChange,
        onCustomDateSelected = { startMillis, endMillis ->
            val startZdt = ZonedDateTime.ofInstant(
                ofEpochMilli(startMillis),
                ZoneId.systemDefault()
            )
            val endZdt = ZonedDateTime.ofInstant(
                ofEpochMilli(endMillis),
                ZoneId.systemDefault()
            )
            viewModel.setCustomDates(startZdt, endZdt)
        }
    )
}

@Composable
fun AppStatisticsScreen(
    appStats: List<UsageApp>,
    hasPermission: Boolean,
    selectedRange: TimeRange,
    customStart: Long?,
    customEnd: Long?,
    onRequestPermission: () -> Unit,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onCustomDateSelected: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val showPicker = remember { mutableStateOf(false) }

    if (showPicker.value) {
        showCustomDatePicker(
            onDatesSelected = { startZdt, endZdt ->
                onCustomDateSelected(
                    startZdt.toInstant().toEpochMilli(),
                    endZdt.toInstant().toEpochMilli()
                )
                showPicker.value = false
            },
            onPickerDismissed = {
                showPicker.value = false
            }
        )
    }

    Box(modifier = modifier) {
        if (!hasPermission) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = onRequestPermission) {
                    Text("Разрешить доступ к статистике")
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                TimeRangeDropdown(
                    selected = selectedRange,
                    onRangeSelected = onTimeRangeSelected,
                    onRequestCustom = { showPicker.value = true }
                )

                if (selectedRange == TimeRange.CUSTOM && customStart != null && customEnd != null) {
                    val formatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
                    val startDate = formatter.format(Date(customStart))
                    val endDate = formatter.format(Date(customEnd))
                    Text(
                        text = "С $startDate по $endDate",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AppStatisticsContent(
                    appStats = appStats,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
internal fun AppStatisticsContent(
    appStats: List<UsageApp>,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = appStats, key = { it.packageName }) { app ->
            AppItem(app)
        }
    }
}

@Composable
fun AppItem(app: UsageApp) {
    val iconPainter: Painter = rememberAsyncImagePainter(model = app.icon)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = iconPainter,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = app.appName)
            val minutes = (app.foregroundTime / 1000) / 60
            Text(text = "Экранное время: $minutes мин")
        }
    }
}

package com.example.app_statistics

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app_statistics.model.TimeRange
import com.example.app_statistics.model.UsageApp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppStatisticsRoute(
    viewModel: AppStatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.appStats.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermissionAndLoadData()
        while (!hasPermission) {
            delay(2000)
            viewModel.checkPermissionAndLoadData()
        }
    }

    val customStart by viewModel.customStart.collectAsState()
    val customEnd by viewModel.customEnd.collectAsState()

    AppStatisticsScreen(
        modifier = Modifier.fillMaxSize(),
        appStats = stats,
        hasPermission = hasPermission,
        selectedRange = selectedRange,
        customStart = customStart,
        customEnd = customEnd,
        onRequestPermission = { viewModel.requestPermission() },
        onTimeRangeSelected = { viewModel.onTimeRangeChange(it) },
        onCustomDateSelected = { start, end -> viewModel.setCustomDates(start, end) }
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
                    onRequestCustom = {
                        showPicker.value = true
                    },
                )

                if (selectedRange == TimeRange.CUSTOM && customStart != null && customEnd != null) {
                    val formatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
                    val startDate = formatter.format(Date(customStart))
                    val endDate = formatter.format(Date(customEnd))
                    Text(
                        text = "С $startDate по $endDate",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AppStatisticsContent(appStats, Modifier.fillMaxSize().padding(16.dp))
            }
        }
    }

}


@Composable
fun TimeRangeDropdown(
    selected: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    onRequestCustom: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.padding(16.dp)) {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Период: ${selected.label}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            TimeRange.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.label) },
                    onClick = {
                        expanded = false
                        if (it == TimeRange.CUSTOM) {
                            onRequestCustom()
                        } else {
                            onRangeSelected(it)
                        }
                    }
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

@Composable
fun showCustomDatePicker(
    onDatesSelected: (Long, Long) -> Unit,
    onPickerDismissed: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val startPickerShown = remember { mutableStateOf(false) }
    val endPickerShown = remember { mutableStateOf(false) }

    val startDate = remember { mutableStateOf<Long?>(null) }

    if (startPickerShown.value) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val startCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0)
                }
                startDate.value = startCal.timeInMillis
                startPickerShown.value = false
                endPickerShown.value = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnDismissListener {
            if (startPickerShown.value) {
                startPickerShown.value = false
                onPickerDismissed()
            }
        }

        dialog.show()
    }

    if (endPickerShown.value && startDate.value != null) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val endCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 23, 59)
                }
                endPickerShown.value = false
                onDatesSelected(startDate.value!!, endCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnDismissListener {
            if (endPickerShown.value) {
                endPickerShown.value = false
                onPickerDismissed()
            }
        }

        dialog.show()
    }

    LaunchedEffect(Unit) {
        startPickerShown.value = true
    }
}



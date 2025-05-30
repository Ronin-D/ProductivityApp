package com.example.app_statistics

import TimeRange
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_statistics.model.UsageApp
import com.example.util.AppUsageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AppStatisticsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _appStats = MutableStateFlow<List<UsageApp>>(emptyList())
    val appStats: StateFlow<List<UsageApp>> = _appStats

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission

    private val _customStart = MutableStateFlow<Long?>(null)
    val customStart: StateFlow<Long?> = _customStart

    private val _customEnd = MutableStateFlow<Long?>(null)
    val customEnd: StateFlow<Long?> = _customEnd

    private val _selectedRange = MutableStateFlow(TimeRange.LAST_7_DAYS)
    val selectedRange: StateFlow<TimeRange> = _selectedRange

    fun setCustomDates(start: ZonedDateTime, end: ZonedDateTime) {
        _customStart.value = start.toInstant().toEpochMilli()
        _customEnd.value = end.toInstant().toEpochMilli()
        _selectedRange.value = TimeRange.CUSTOM
        loadUsageStats()
    }


    fun checkPermissionAndLoadData() {
        if (AppUsageHelper.hasUsageStatsPermission(context)) {
            _hasPermission.value = true
            loadUsageStats()
        } else {
            _hasPermission.value = false
        }
    }

    fun requestPermission() {
        AppUsageHelper.requestUsageStatsPermission(context)
    }

    private fun loadUsageStats() {
        viewModelScope.launch {
            val end = when (_selectedRange.value) {
                TimeRange.CUSTOM -> _customEnd.value ?: System.currentTimeMillis()
                else -> System.currentTimeMillis()
            }

            val start = when (val range = _selectedRange.value) {
                TimeRange.CUSTOM -> _customStart.value ?: (end - TimeUnit.DAYS.toMillis(1))
                else -> end - TimeUnit.DAYS.toMillis(range.days!!.toLong())
            }

            val stats = AppUsageHelper.getUsageStats(context, start, end)

            val aggregated = stats.groupBy { it.packageName }.mapNotNull { (packageName, list) ->
                val totalForeground = list.sumOf { it.totalTimeInForeground }
                val appInfo = AppUsageHelper.getAppNameAndIcon(context, packageName)
                appInfo?.let { (name, icon) ->
                    UsageApp(name, icon, totalForeground, packageName)
                }
            }.sortedByDescending { it.foregroundTime }

            _appStats.value = aggregated
        }
    }


    fun onTimeRangeChange(range: TimeRange) {
        _selectedRange.value = range
        loadUsageStats()
    }
}

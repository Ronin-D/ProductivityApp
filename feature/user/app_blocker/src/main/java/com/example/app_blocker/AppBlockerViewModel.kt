package com.example.app_blocker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.models.InstalledApp
import com.example.data_store.AppBlockDataStore
import com.example.util.InstalledAppsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppBlockerViewModel @Inject constructor(
    private val appBlockDataStore: AppBlockDataStore
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps

    fun loadInstalledApps(context: Context) {
        _installedApps.value = InstalledAppsHelper.getInstalledApps(context)
    }

    fun blockApp(packageName: String, durationMillis: Long) {
        viewModelScope.launch {
            appBlockDataStore.blockApp(packageName, durationMillis)
        }
    }
}



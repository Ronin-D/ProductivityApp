package com.example.blocked_app_list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_store.AppBlockDataStore
import com.example.util.InstalledAppsHelper
import com.example.util.models.InstalledApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedAppListViewModel @Inject constructor(
    private val appBlockDataStore: AppBlockDataStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val _blockedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val blockedApps: StateFlow<List<InstalledApp>> = this._blockedApps
    val shouldRefresh = savedStateHandle.getStateFlow("shouldRefresh", false)

    fun loadBlockedApps(context: Context) {
        this._installedApps.value = InstalledAppsHelper.getInstalledApps(context)
        viewModelScope.launch(Dispatchers.IO) {
            val blockedAppInfos = appBlockDataStore.getBlockedApps().map { it.packageName }
            _blockedApps.value = _installedApps.value.filter { app ->
                app.packageName in blockedAppInfos
            }.onEach { app ->
                app.unlockDurationMillis = appBlockDataStore.getUnblockTime(app.packageName)
            }
            savedStateHandle["shouldRefresh"] = false
        }
    }

    fun unblockApp(packageName: String) {
        viewModelScope.launch {
            appBlockDataStore.unblockApp(packageName)
            _blockedApps.value = _blockedApps.value.filter { it.packageName != packageName }
        }
    }
}

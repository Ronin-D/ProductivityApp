package com.example.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_store.AuthDataStoreRepository
import com.example.network.api.UserApi
import com.example.network.api.dto.SendStatisticsRequest
import com.example.network.api.dto.UsageStatDto
import com.example.util.AppUsageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userApi: UserApi,
    private val authDataStoreRepository: AuthDataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _sendStatisticsUiState =
        MutableStateFlow<SendStatisticsUiState>(SendStatisticsUiState.Idle)
    val sendStatisticsUiState: StateFlow<SendStatisticsUiState> = _sendStatisticsUiState

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = ProfileUiState.Success(userApi.getUserData())
            } catch (e: Exception) {
                Log.e("profile", e.message.toString())
                _uiState.value = ProfileUiState.Error(e.message.toString())
            }
        }
    }

    fun retry() {
        _uiState.value = ProfileUiState.Loading
        loadUserData()
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authDataStoreRepository.clearTokens()
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun sendStatistics(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _sendStatisticsUiState.value = SendStatisticsUiState.Loading
            try {
                val zoneId = ZoneId.systemDefault()
                val startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfNow = Instant.now().toEpochMilli()
                val stats = AppUsageHelper.getUsageStats(context, startOfDay, endOfNow)
                val apps = stats.mapNotNull {
                    val appInfo = AppUsageHelper.getAppNameAndIcon(context, it.packageName)
                    appInfo?.let { (name, _) ->
                        UsageStatDto(it.packageName, name, it.totalTimeInForeground)
                    }
                }
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zoneId)
                val formattedDate = formatter.format(Instant.ofEpochMilli(endOfNow))
                val request = SendStatisticsRequest(formattedDate, apps)
                userApi.sendStatistics(request)
                _sendStatisticsUiState.value = SendStatisticsUiState.Success
            } catch (e: Exception) {
                Log.e("sendStatistics", e.message.toString())
                _sendStatisticsUiState.value =
                    SendStatisticsUiState.Error(e.localizedMessage ?: "Ошибка отправки статистики")
            }
        }
    }

    fun resetSendStatisticsState() {
        _sendStatisticsUiState.value = SendStatisticsUiState.Idle
    }

}
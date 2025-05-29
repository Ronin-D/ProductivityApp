package com.example.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_store.AuthDataStoreRepository
import com.example.network.api.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DoctorProfileViewModel @Inject constructor(
    private val userApi: UserApi,
    private val authDataStoreRepository: AuthDataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DoctorProfileUiState>(DoctorProfileUiState.Loading)
    val uiState: StateFlow<DoctorProfileUiState> = _uiState

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = DoctorProfileUiState.Success(userApi.getDoctorData())
            } catch (e: Exception) {
                Log.e("profile", e.message.toString())
                _uiState.value = DoctorProfileUiState.Error(e.message.toString())
            }
        }
    }

    fun retry() {
        _uiState.value = DoctorProfileUiState.Loading
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
}
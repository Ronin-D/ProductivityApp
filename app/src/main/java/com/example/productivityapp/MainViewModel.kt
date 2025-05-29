package com.example.productivityapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_store.AuthDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authDataStore: AuthDataStoreRepository
) : ViewModel() {
    val role: StateFlow<String?> = authDataStore.getUserRole()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

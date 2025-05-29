package com.example.sign_in

import com.example.api.dto.LoginResponse

sealed interface SignInUiState {

    object Loading : SignInUiState

    object NotSet : SignInUiState

    data class Error(
        val message: String
    ) : SignInUiState

    data class Success(val loginResponse: LoginResponse) : SignInUiState
}
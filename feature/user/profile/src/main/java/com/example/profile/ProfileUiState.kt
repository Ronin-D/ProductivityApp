package com.example.profile

import com.example.network.api.dto.UserDto

sealed interface ProfileUiState {

    object Loading : ProfileUiState

    data class Error(
        val message: String
    ) : ProfileUiState

    data class Success(
        val userDto: UserDto
    ) : ProfileUiState
}
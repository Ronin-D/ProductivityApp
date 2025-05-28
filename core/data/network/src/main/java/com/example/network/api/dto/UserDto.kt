package com.example.network.api.dto

data class UserDto(
    val name: String,
    val surname: String,
    val patronymic: String,
    val role: String,
    val email: String,
    val doctors: List<DoctorDto>
)

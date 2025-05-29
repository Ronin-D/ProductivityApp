package com.example.network.api

import com.example.network.api.dto.DoctorProfileDto
import com.example.network.api.dto.SendStatisticsRequest
import com.example.network.api.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @GET("patient")
    suspend fun getUserData(): UserDto

    @GET("doctor")
    suspend fun getDoctorData(): DoctorProfileDto

    @POST("user/statistics")
    suspend fun sendStatistics(@Body request: SendStatisticsRequest)
}
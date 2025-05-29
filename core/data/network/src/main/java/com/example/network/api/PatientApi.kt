package com.example.network.api

import com.example.network.api.dto.PatientDto
import com.example.network.api.dto.PatientStatisticsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.ZonedDateTime

interface PatientApi {

    @GET(".")
    suspend fun getPatients(): List<PatientDto>

    @GET("{patientId}/statistics")
    suspend fun getPatientStatistics(
        @Path("patientId") patientId: String,
        @Query("from") from: ZonedDateTime,
        @Query("to") to: ZonedDateTime
    ): PatientStatisticsResponse
}
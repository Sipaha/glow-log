package com.glowlog.app.data.repository

import com.glowlog.app.domain.model.BloodPressureReading
import kotlinx.coroutines.flow.Flow

interface BloodPressureRepository {
    fun getAllReadings(): Flow<List<BloodPressureReading>>
    fun getReadingsByDateRange(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReading>>
    fun getReadingsByDateRangeAsc(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReading>>
    fun getRecentReadings(limit: Int): Flow<List<BloodPressureReading>>
    suspend fun getReadingById(id: String): BloodPressureReading?
    suspend fun addReading(reading: BloodPressureReading)
    suspend fun updateReading(reading: BloodPressureReading)
    suspend fun deleteReading(id: String)
}

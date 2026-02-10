package com.glowlog.app.data.repository

import com.glowlog.app.domain.model.GlucoseReading
import kotlinx.coroutines.flow.Flow

interface GlucoseRepository {
    fun getAllReadings(): Flow<List<GlucoseReading>>
    fun getReadingsByDateRange(startEpoch: Long, endEpoch: Long): Flow<List<GlucoseReading>>
    fun getReadingsByDateRangeAsc(startEpoch: Long, endEpoch: Long): Flow<List<GlucoseReading>>
    fun getRecentReadings(limit: Int): Flow<List<GlucoseReading>>
    suspend fun getReadingById(id: String): GlucoseReading?
    suspend fun addReading(reading: GlucoseReading)
    suspend fun updateReading(reading: GlucoseReading)
    suspend fun deleteReading(id: String)
}

package com.glowlog.app.data.repository

import com.glowlog.app.data.local.db.dao.BloodPressureReadingDao
import com.glowlog.app.data.local.db.entity.BloodPressureReadingEntity
import com.glowlog.app.data.sync.SyncManager
import com.glowlog.app.domain.model.Arm
import com.glowlog.app.domain.model.BloodPressureReading
import com.glowlog.app.domain.model.TimeOfDay
import com.glowlog.app.ui.common.util.ReadingStatusUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloodPressureRepositoryImpl @Inject constructor(
    private val dao: BloodPressureReadingDao,
    private val syncManager: SyncManager
) : BloodPressureRepository {

    override fun getAllReadings(): Flow<List<BloodPressureReading>> {
        return dao.getAllFlow().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getReadingsByDateRange(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReading>> {
        return dao.getByDateRangeFlow(startEpoch, endEpoch).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getReadingsByDateRangeAsc(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReading>> {
        return dao.getByDateRangeAscFlow(startEpoch, endEpoch).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecentReadings(limit: Int): Flow<List<BloodPressureReading>> {
        return dao.getRecentFlow(limit).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getReadingById(id: String): BloodPressureReading? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun addReading(reading: BloodPressureReading) {
        val now = System.currentTimeMillis()
        dao.insert(reading.toEntity(createdAt = now, updatedAt = now))
        syncManager.triggerSync()
    }

    override suspend fun updateReading(reading: BloodPressureReading) {
        val existing = dao.getById(reading.id) ?: return
        val now = System.currentTimeMillis()
        dao.update(reading.toEntity(createdAt = existing.createdAt, updatedAt = now))
        syncManager.triggerSync()
    }

    override suspend fun deleteReading(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
        syncManager.triggerSync()
    }
}

fun BloodPressureReadingEntity.toDomain(): BloodPressureReading {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(measuredAt), ZoneId.systemDefault())
    val armEnum = try { Arm.valueOf(arm) } catch (_: Exception) { Arm.LEFT }
    val todEnum = try { TimeOfDay.valueOf(timeOfDay) } catch (_: Exception) { TimeOfDay.MORNING }
    return BloodPressureReading(
        id = id,
        systolic = systolic,
        diastolic = diastolic,
        pulse = pulse,
        arm = armEnum,
        timeOfDay = todEnum,
        measuredAt = dateTime,
        note = note,
        status = ReadingStatusUtil.bloodPressureStatus(systolic, diastolic)
    )
}

fun BloodPressureReading.toEntity(createdAt: Long, updatedAt: Long): BloodPressureReadingEntity {
    return BloodPressureReadingEntity(
        id = id,
        systolic = systolic,
        diastolic = diastolic,
        pulse = pulse,
        arm = arm.name,
        timeOfDay = timeOfDay.name,
        measuredAt = measuredAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = false
    )
}

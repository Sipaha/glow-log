package com.glowlog.app.data.repository

import com.glowlog.app.data.local.db.dao.GlucoseReadingDao
import com.glowlog.app.data.local.db.entity.GlucoseReadingEntity
import com.glowlog.app.data.sync.SyncManager
import com.glowlog.app.domain.model.GlucoseReading
import com.glowlog.app.domain.model.MealContext
import com.glowlog.app.ui.common.util.ReadingStatusUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlucoseRepositoryImpl @Inject constructor(
    private val dao: GlucoseReadingDao,
    private val syncManager: SyncManager
) : GlucoseRepository {

    override fun getAllReadings(): Flow<List<GlucoseReading>> {
        return dao.getAllFlow().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getReadingsByDateRange(startEpoch: Long, endEpoch: Long): Flow<List<GlucoseReading>> {
        return dao.getByDateRangeFlow(startEpoch, endEpoch).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getReadingsByDateRangeAsc(startEpoch: Long, endEpoch: Long): Flow<List<GlucoseReading>> {
        return dao.getByDateRangeAscFlow(startEpoch, endEpoch).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecentReadings(limit: Int): Flow<List<GlucoseReading>> {
        return dao.getRecentFlow(limit).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getReadingById(id: String): GlucoseReading? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun addReading(reading: GlucoseReading) {
        val now = System.currentTimeMillis()
        dao.insert(reading.toEntity(createdAt = now, updatedAt = now))
        syncManager.triggerSync()
    }

    override suspend fun updateReading(reading: GlucoseReading) {
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

fun GlucoseReadingEntity.toDomain(): GlucoseReading {
    val mealCtx = try {
        MealContext.valueOf(mealContext)
    } catch (_: Exception) {
        MealContext.FASTING
    }
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(measuredAt), ZoneOffset.UTC)
    return GlucoseReading(
        id = id,
        valueMmol = valueMmol,
        mealContext = mealCtx,
        measuredAt = dateTime,
        note = note,
        status = ReadingStatusUtil.glucoseStatus(valueMmol, mealCtx)
    )
}

fun GlucoseReading.toEntity(createdAt: Long, updatedAt: Long): GlucoseReadingEntity {
    return GlucoseReadingEntity(
        id = id,
        valueMmol = valueMmol,
        mealContext = mealContext.name,
        measuredAt = measuredAt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = false
    )
}

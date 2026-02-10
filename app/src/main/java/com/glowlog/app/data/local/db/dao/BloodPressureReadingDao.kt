package com.glowlog.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.glowlog.app.data.local.db.entity.BloodPressureReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureReadingDao {

    @Query("SELECT * FROM blood_pressure_readings WHERE isDeleted = 0 ORDER BY measuredAt DESC")
    fun getAllFlow(): Flow<List<BloodPressureReadingEntity>>

    @Query("SELECT * FROM blood_pressure_readings WHERE isDeleted = 0 AND measuredAt BETWEEN :startEpoch AND :endEpoch ORDER BY measuredAt DESC")
    fun getByDateRangeFlow(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReadingEntity>>

    @Query("SELECT * FROM blood_pressure_readings WHERE isDeleted = 0 AND measuredAt BETWEEN :startEpoch AND :endEpoch ORDER BY measuredAt ASC")
    fun getByDateRangeAscFlow(startEpoch: Long, endEpoch: Long): Flow<List<BloodPressureReadingEntity>>

    @Query("SELECT * FROM blood_pressure_readings WHERE id = :id")
    suspend fun getById(id: String): BloodPressureReadingEntity?

    @Query("SELECT * FROM blood_pressure_readings WHERE isDeleted = 0 ORDER BY measuredAt DESC LIMIT :limit")
    fun getRecentFlow(limit: Int): Flow<List<BloodPressureReadingEntity>>

    @Query("SELECT * FROM blood_pressure_readings WHERE isDeleted = 0 ORDER BY measuredAt DESC")
    suspend fun getAll(): List<BloodPressureReadingEntity>

    @Query("SELECT * FROM blood_pressure_readings WHERE isSynced = 0")
    suspend fun getUnsynced(): List<BloodPressureReadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BloodPressureReadingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<BloodPressureReadingEntity>)

    @Update
    suspend fun update(entity: BloodPressureReadingEntity)

    @Query("UPDATE blood_pressure_readings SET isDeleted = 1, updatedAt = :updatedAt, isSynced = 0 WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long)

    @Query("UPDATE blood_pressure_readings SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT COUNT(*) FROM blood_pressure_readings WHERE isDeleted = 0 AND measuredAt BETWEEN :startEpoch AND :endEpoch")
    fun getCountByDateRange(startEpoch: Long, endEpoch: Long): Flow<Int>
}

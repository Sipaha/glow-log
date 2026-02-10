package com.glowlog.app.data.repository

import android.util.Log
import com.glowlog.app.data.local.datastore.UserPreferences
import com.glowlog.app.data.local.db.dao.BloodPressureReadingDao
import com.glowlog.app.data.local.db.dao.GlucoseReadingDao
import com.glowlog.app.data.local.db.entity.BloodPressureReadingEntity
import com.glowlog.app.data.local.db.entity.GlucoseReadingEntity
import com.glowlog.app.data.remote.auth.FirebaseAuthManager
import com.glowlog.app.data.remote.firestore.FirestoreBloodPressureSource
import com.glowlog.app.data.remote.firestore.FirestoreGlucoseSource
import com.glowlog.app.data.remote.firestore.dto.BloodPressureReadingDto
import com.glowlog.app.data.remote.firestore.dto.GlucoseReadingDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val glucoseDao: GlucoseReadingDao,
    private val bloodPressureDao: BloodPressureReadingDao,
    private val firestoreGlucose: FirestoreGlucoseSource,
    private val firestoreBloodPressure: FirestoreBloodPressureSource,
    private val authManager: FirebaseAuthManager,
    private val userPreferences: UserPreferences
) {
    suspend fun sync(): Result<Unit> {
        val userId = authManager.currentUserId
        if (userId == null) {
            Log.d(TAG, "Sync skipped: user not signed in")
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            Log.d(TAG, "Sync started for user=$userId")
            pushGlucose(userId)
            pushBloodPressure(userId)

            val lastSync = userPreferences.lastSyncTimestamp.first()
            Log.d(TAG, "Pulling changes since $lastSync")
            pullGlucose(userId, lastSync)
            pullBloodPressure(userId, lastSync)

            userPreferences.updateLastSyncTimestamp(System.currentTimeMillis())
            Log.d(TAG, "Sync completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "GlowLog.Sync"
    }

    private suspend fun pushGlucose(userId: String) {
        val unsynced = glucoseDao.getUnsynced()
        if (unsynced.isEmpty()) return
        Log.d(TAG, "Pushing ${unsynced.size} glucose readings")

        val dtos = unsynced.map { entity ->
            GlucoseReadingDto(
                id = entity.id,
                valueMmol = entity.valueMmol,
                mealContext = entity.mealContext,
                measuredAt = entity.measuredAt,
                note = entity.note,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isDeleted = entity.isDeleted
            )
        }
        firestoreGlucose.pushReadings(userId, dtos)
        unsynced.forEach { glucoseDao.markSynced(it.id) }
    }

    private suspend fun pushBloodPressure(userId: String) {
        val unsynced = bloodPressureDao.getUnsynced()
        if (unsynced.isEmpty()) return
        Log.d(TAG, "Pushing ${unsynced.size} blood pressure readings")

        val dtos = unsynced.map { entity ->
            BloodPressureReadingDto(
                id = entity.id,
                systolic = entity.systolic,
                diastolic = entity.diastolic,
                pulse = entity.pulse,
                arm = entity.arm,
                timeOfDay = entity.timeOfDay,
                measuredAt = entity.measuredAt,
                note = entity.note,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isDeleted = entity.isDeleted
            )
        }
        firestoreBloodPressure.pushReadings(userId, dtos)
        unsynced.forEach { bloodPressureDao.markSynced(it.id) }
    }

    private suspend fun pullGlucose(userId: String, sinceTimestamp: Long) {
        val remoteDtos = firestoreGlucose.pullReadings(userId, sinceTimestamp)
        for (dto in remoteDtos) {
            val local = glucoseDao.getById(dto.id)
            // Only overwrite if remote is newer or record doesn't exist locally
            if (local == null || dto.updatedAt > local.updatedAt) {
                val entity = GlucoseReadingEntity(
                    id = dto.id,
                    valueMmol = dto.valueMmol,
                    mealContext = dto.mealContext,
                    measuredAt = dto.measuredAt,
                    note = dto.note,
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt,
                    isDeleted = dto.isDeleted,
                    isSynced = true,
                    firebaseUserId = userId
                )
                glucoseDao.insert(entity)
            }
        }
    }

    private suspend fun pullBloodPressure(userId: String, sinceTimestamp: Long) {
        val remoteDtos = firestoreBloodPressure.pullReadings(userId, sinceTimestamp)
        for (dto in remoteDtos) {
            val local = bloodPressureDao.getById(dto.id)
            if (local == null || dto.updatedAt > local.updatedAt) {
                val entity = BloodPressureReadingEntity(
                    id = dto.id,
                    systolic = dto.systolic,
                    diastolic = dto.diastolic,
                    pulse = dto.pulse,
                    arm = dto.arm,
                    timeOfDay = dto.timeOfDay,
                    measuredAt = dto.measuredAt,
                    note = dto.note,
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt,
                    isDeleted = dto.isDeleted,
                    isSynced = true,
                    firebaseUserId = userId
                )
                bloodPressureDao.insert(entity)
            }
        }
    }
}

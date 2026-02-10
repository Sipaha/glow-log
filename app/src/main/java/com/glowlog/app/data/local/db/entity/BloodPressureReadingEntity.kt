package com.glowlog.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_readings")
data class BloodPressureReadingEntity(
    @PrimaryKey val id: String,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val arm: String,
    val timeOfDay: String,
    val measuredAt: Long,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val firebaseUserId: String? = null
)

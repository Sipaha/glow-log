package com.glowlog.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glucose_readings")
data class GlucoseReadingEntity(
    @PrimaryKey val id: String,
    val valueMmol: Double,
    val mealContext: String,
    val measuredAt: Long,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val firebaseUserId: String? = null
)

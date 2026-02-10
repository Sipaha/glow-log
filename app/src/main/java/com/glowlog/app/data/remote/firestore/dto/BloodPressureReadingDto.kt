package com.glowlog.app.data.remote.firestore.dto

data class BloodPressureReadingDto(
    override val id: String = "",
    val systolic: Int = 0,
    val diastolic: Int = 0,
    val pulse: Int? = null,
    val arm: String = "LEFT",
    val timeOfDay: String = "MORNING",
    val measuredAt: Long = 0,
    val note: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val isDeleted: Boolean = false
) : FirestoreDto

package com.glowlog.app.domain.model

import java.time.LocalDateTime

data class BloodPressureReading(
    val id: String,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val arm: Arm,
    val timeOfDay: TimeOfDay,
    val measuredAt: LocalDateTime,
    val note: String?,
    val status: ReadingStatus
)

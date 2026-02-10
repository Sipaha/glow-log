package com.glowlog.app.domain.model

import java.time.LocalDateTime

data class GlucoseReading(
    val id: String,
    val valueMmol: Double,
    val mealContext: MealContext,
    val measuredAt: LocalDateTime,
    val note: String?,
    val status: ReadingStatus
)

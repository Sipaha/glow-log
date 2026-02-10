package com.glowlog.app.data.remote.firestore.dto

data class GlucoseReadingDto(
    override val id: String = "",
    val valueMmol: Double = 0.0,
    val mealContext: String = "",
    val measuredAt: Long = 0,
    val note: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val isDeleted: Boolean = false
) : FirestoreDto

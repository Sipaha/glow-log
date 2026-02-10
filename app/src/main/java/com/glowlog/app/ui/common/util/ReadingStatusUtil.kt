package com.glowlog.app.ui.common.util

import com.glowlog.app.domain.model.MealContext
import com.glowlog.app.domain.model.ReadingStatus

object ReadingStatusUtil {

    fun glucoseStatus(valueMmol: Double, mealContext: MealContext): ReadingStatus {
        return when (mealContext) {
            MealContext.FASTING, MealContext.BEFORE_MEAL -> when {
                valueMmol <= 5.1 -> ReadingStatus.NORMAL
                valueMmol <= 5.6 -> ReadingStatus.BORDERLINE
                else -> ReadingStatus.HIGH
            }
            MealContext.AFTER_MEAL_1H -> when {
                valueMmol <= 10.0 -> ReadingStatus.NORMAL
                valueMmol <= 11.0 -> ReadingStatus.BORDERLINE
                else -> ReadingStatus.HIGH
            }
            MealContext.AFTER_MEAL_2H -> when {
                valueMmol <= 8.5 -> ReadingStatus.NORMAL
                valueMmol <= 9.3 -> ReadingStatus.BORDERLINE
                else -> ReadingStatus.HIGH
            }
        }
    }

    fun bloodPressureStatus(systolic: Int, diastolic: Int): ReadingStatus {
        return when {
            systolic < 140 && diastolic < 90 -> ReadingStatus.NORMAL
            systolic <= 150 && diastolic <= 95 -> ReadingStatus.BORDERLINE
            else -> ReadingStatus.HIGH
        }
    }
}

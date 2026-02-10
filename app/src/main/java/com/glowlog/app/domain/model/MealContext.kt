package com.glowlog.app.domain.model

import androidx.annotation.StringRes
import com.glowlog.app.R

enum class MealContext(@get:StringRes val labelRes: Int) {
    FASTING(R.string.meal_fasting),
    BEFORE_MEAL(R.string.meal_before),
    AFTER_MEAL_1H(R.string.meal_after_1h),
    AFTER_MEAL_2H(R.string.meal_after_2h)
}

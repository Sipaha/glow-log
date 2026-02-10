package com.glowlog.app.domain.model

import androidx.annotation.StringRes
import com.glowlog.app.R

enum class TimeOfDay(@get:StringRes val labelRes: Int) {
    MORNING(R.string.tod_morning),
    DAY(R.string.tod_day),
    EVENING(R.string.tod_evening),
    NIGHT(R.string.tod_night)
}

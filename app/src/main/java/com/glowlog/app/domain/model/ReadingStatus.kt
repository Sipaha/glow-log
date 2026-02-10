package com.glowlog.app.domain.model

import androidx.annotation.StringRes
import com.glowlog.app.R

enum class ReadingStatus(@get:StringRes val labelRes: Int) {
    NORMAL(R.string.status_normal),
    BORDERLINE(R.string.status_borderline),
    HIGH(R.string.status_high)
}

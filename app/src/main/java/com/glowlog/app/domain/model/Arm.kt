package com.glowlog.app.domain.model

import androidx.annotation.StringRes
import com.glowlog.app.R

enum class Arm(@get:StringRes val labelRes: Int) {
    LEFT(R.string.arm_left),
    RIGHT(R.string.arm_right)
}

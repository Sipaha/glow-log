package com.glowlog.app.domain.model

import java.time.LocalDate
import java.time.ZoneOffset

sealed class DateRange {
    data object Week : DateRange()
    data object Month : DateRange()
    data class Custom(val start: LocalDate, val end: LocalDate) : DateRange()

    fun toEpochMillis(): Pair<Long, Long> {
        val now = LocalDate.now()
        val (start, end) = when (this) {
            is Week -> now.minusDays(7) to now
            is Month -> now.minusMonths(1) to now
            is Custom -> this.start to this.end
        }
        return start.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() to
                end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}

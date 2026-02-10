package com.glowlog.app.ui.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeFormatters {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)

    fun formatDate(dateTime: LocalDateTime): String = dateTime.format(dateFormatter)
    fun formatTime(dateTime: LocalDateTime): String = dateTime.format(timeFormatter)
    fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)
    fun formatShortDate(dateTime: LocalDateTime): String = dateTime.format(shortDateFormatter)
    fun formatDate(date: LocalDate): String = date.format(dateFormatter)
}

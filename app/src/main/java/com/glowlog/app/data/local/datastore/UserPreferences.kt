package com.glowlog.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.glowlog.app.domain.model.TimeOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class ReminderSettings(
    val morningEnabled: Boolean = false,
    val morningTime: String = "08:00",
    val eveningEnabled: Boolean = false,
    val eveningTime: String = "20:00",
    val afterMealEnabled: Boolean = false
)

/**
 * Time-of-day boundaries (hour of day, 0–23).
 * Each value marks the START of that period.
 * Default: Утро 06:00, День 12:00, Вечер 18:00, Ночь 22:00
 */
data class TimeOfDayRanges(
    val morningStart: Int = 6,
    val dayStart: Int = 12,
    val eveningStart: Int = 18,
    val nightStart: Int = 22
) {
    fun resolve(time: LocalTime): TimeOfDay {
        val hour = time.hour
        return when {
            hour >= nightStart || hour < morningStart -> TimeOfDay.NIGHT
            hour < dayStart -> TimeOfDay.MORNING
            hour < eveningStart -> TimeOfDay.DAY
            else -> TimeOfDay.EVENING
        }
    }
}

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val MORNING_REMINDER_ENABLED = booleanPreferencesKey("morning_reminder_enabled")
        val MORNING_REMINDER_TIME = stringPreferencesKey("morning_reminder_time")
        val EVENING_REMINDER_ENABLED = booleanPreferencesKey("evening_reminder_enabled")
        val EVENING_REMINDER_TIME = stringPreferencesKey("evening_reminder_time")
        val AFTER_MEAL_REMINDER_ENABLED = booleanPreferencesKey("after_meal_reminder_enabled")
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")

        val TOD_MORNING_START = intPreferencesKey("tod_morning_start")
        val TOD_DAY_START = intPreferencesKey("tod_day_start")
        val TOD_EVENING_START = intPreferencesKey("tod_evening_start")
        val TOD_NIGHT_START = intPreferencesKey("tod_night_start")
    }

    val reminderSettings: Flow<ReminderSettings> = dataStore.data.map { prefs ->
        ReminderSettings(
            morningEnabled = prefs[MORNING_REMINDER_ENABLED] ?: false,
            morningTime = prefs[MORNING_REMINDER_TIME] ?: "08:00",
            eveningEnabled = prefs[EVENING_REMINDER_ENABLED] ?: false,
            eveningTime = prefs[EVENING_REMINDER_TIME] ?: "20:00",
            afterMealEnabled = prefs[AFTER_MEAL_REMINDER_ENABLED] ?: false
        )
    }

    val timeOfDayRanges: Flow<TimeOfDayRanges> = dataStore.data.map { prefs ->
        TimeOfDayRanges(
            morningStart = prefs[TOD_MORNING_START] ?: 6,
            dayStart = prefs[TOD_DAY_START] ?: 12,
            eveningStart = prefs[TOD_EVENING_START] ?: 18,
            nightStart = prefs[TOD_NIGHT_START] ?: 22
        )
    }

    val lastSyncTimestamp: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_SYNC_TIMESTAMP] ?: 0L
    }

    suspend fun updateReminderSettings(settings: ReminderSettings) {
        dataStore.edit { prefs ->
            prefs[MORNING_REMINDER_ENABLED] = settings.morningEnabled
            prefs[MORNING_REMINDER_TIME] = settings.morningTime
            prefs[EVENING_REMINDER_ENABLED] = settings.eveningEnabled
            prefs[EVENING_REMINDER_TIME] = settings.eveningTime
            prefs[AFTER_MEAL_REMINDER_ENABLED] = settings.afterMealEnabled
        }
    }

    suspend fun updateTimeOfDayRanges(ranges: TimeOfDayRanges) {
        dataStore.edit { prefs ->
            prefs[TOD_MORNING_START] = ranges.morningStart
            prefs[TOD_DAY_START] = ranges.dayStart
            prefs[TOD_EVENING_START] = ranges.eveningStart
            prefs[TOD_NIGHT_START] = ranges.nightStart
        }
    }

    suspend fun updateLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }
}

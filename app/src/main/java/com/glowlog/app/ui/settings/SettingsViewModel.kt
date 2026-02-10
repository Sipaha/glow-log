package com.glowlog.app.ui.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.export.CsvExporter
import com.glowlog.app.data.local.datastore.ReminderSettings
import com.glowlog.app.data.local.datastore.TimeOfDayRanges
import com.glowlog.app.data.local.datastore.UserPreferences
import com.glowlog.app.data.repository.AuthRepository
import com.glowlog.app.domain.model.UserProfile
import com.glowlog.app.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShareEvent(val uri: Uri, val fileName: String)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val csvExporter: CsvExporter,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val reminderSettings: StateFlow<ReminderSettings> = userPreferences.reminderSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderSettings())

    val timeOfDayRanges: StateFlow<TimeOfDayRanges> = userPreferences.timeOfDayRanges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeOfDayRanges())

    private val _shareEvent = MutableSharedFlow<ShareEvent>()
    val shareEvent: SharedFlow<ShareEvent> = _shareEvent.asSharedFlow()

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun updateReminderSettings(settings: ReminderSettings) {
        viewModelScope.launch {
            userPreferences.updateReminderSettings(settings)
            reminderScheduler.scheduleReminders(settings)
        }
    }

    fun updateTimeOfDayRanges(ranges: TimeOfDayRanges) {
        // Validate ordering: morning < day < evening < night
        if (ranges.morningStart >= ranges.dayStart ||
            ranges.dayStart >= ranges.eveningStart ||
            ranges.eveningStart >= ranges.nightStart
        ) return
        viewModelScope.launch {
            userPreferences.updateTimeOfDayRanges(ranges)
        }
    }

    fun exportGlucoseCsv() {
        viewModelScope.launch {
            try {
                val uri = csvExporter.exportGlucoseCsv()
                _shareEvent.emit(ShareEvent(uri, "glucose_readings.csv"))
            } catch (e: Exception) {
                Log.e("GlowLog.Export", "Glucose CSV export failed", e)
            }
        }
    }

    fun exportBloodPressureCsv() {
        viewModelScope.launch {
            try {
                val uri = csvExporter.exportBloodPressureCsv()
                _shareEvent.emit(ShareEvent(uri, "blood_pressure_readings.csv"))
            } catch (e: Exception) {
                Log.e("GlowLog.Export", "Blood pressure CSV export failed", e)
            }
        }
    }
}

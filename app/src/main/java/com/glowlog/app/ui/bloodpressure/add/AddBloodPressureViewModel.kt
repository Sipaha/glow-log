package com.glowlog.app.ui.bloodpressure.add

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.R
import com.glowlog.app.data.local.datastore.TimeOfDayRanges
import com.glowlog.app.data.local.datastore.UserPreferences
import com.glowlog.app.data.repository.BloodPressureRepository
import com.glowlog.app.domain.model.Arm
import com.glowlog.app.domain.model.BloodPressureReading
import com.glowlog.app.domain.model.ReadingStatus
import com.glowlog.app.domain.model.TimeOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

data class AddBloodPressureUiState(
    val systolic: String = "",
    val diastolic: String = "",
    val pulse: String = "",
    val arm: Arm = Arm.LEFT,
    val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val note: String = "",
    val isEditing: Boolean = false,
    val editingId: String? = null,
    val isSaved: Boolean = false,
    @StringRes val systolicError: Int? = null,
    @StringRes val diastolicError: Int? = null
)

@HiltViewModel
class AddBloodPressureViewModel @Inject constructor(
    private val repository: BloodPressureRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBloodPressureUiState())
    val uiState: StateFlow<AddBloodPressureUiState> = _uiState.asStateFlow()

    private var todRanges = TimeOfDayRanges()

    init {
        viewModelScope.launch {
            todRanges = userPreferences.timeOfDayRanges.first()

            val readingId = savedStateHandle.get<String>("readingId")
            if (readingId != null) {
                val reading = repository.getReadingById(readingId)
                if (reading != null) {
                    _uiState.value = AddBloodPressureUiState(
                        systolic = reading.systolic.toString(),
                        diastolic = reading.diastolic.toString(),
                        pulse = reading.pulse?.toString() ?: "",
                        arm = reading.arm,
                        timeOfDay = reading.timeOfDay,
                        date = reading.measuredAt.toLocalDate(),
                        time = reading.measuredAt.toLocalTime(),
                        note = reading.note ?: "",
                        isEditing = true,
                        editingId = reading.id
                    )
                }
            } else {
                // Resolve initial timeOfDay from current time only for new readings
                _uiState.value = _uiState.value.copy(
                    timeOfDay = todRanges.resolve(_uiState.value.time)
                )
            }
        }
    }

    fun onSystolicChange(value: String) {
        _uiState.value = _uiState.value.copy(systolic = value, systolicError = null)
    }

    fun onDiastolicChange(value: String) {
        _uiState.value = _uiState.value.copy(diastolic = value, diastolicError = null)
    }

    fun onPulseChange(value: String) {
        _uiState.value = _uiState.value.copy(pulse = value)
    }

    fun onArmChange(arm: Arm) {
        _uiState.value = _uiState.value.copy(arm = arm)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onTimeChange(time: LocalTime) {
        _uiState.value = _uiState.value.copy(
            time = time,
            timeOfDay = todRanges.resolve(time)
        )
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaved) return

        val systolic = state.systolic.toIntOrNull()
        val diastolic = state.diastolic.toIntOrNull()
        val pulse = state.pulse.toIntOrNull()

        var hasError = false
        var newState = state

        if (systolic == null || systolic <= 0 || systolic > 300) {
            newState = newState.copy(systolicError = R.string.error_systolic_value)
            hasError = true
        }
        if (diastolic == null || diastolic <= 0 || diastolic > 200) {
            newState = newState.copy(diastolicError = R.string.error_diastolic_value)
            hasError = true
        }

        if (hasError) {
            _uiState.value = newState
            return
        }

        _uiState.value = state.copy(isSaved = true)
        viewModelScope.launch {
            val reading = BloodPressureReading(
                id = state.editingId ?: UUID.randomUUID().toString(),
                systolic = systolic!!,
                diastolic = diastolic!!,
                pulse = pulse,
                arm = state.arm,
                timeOfDay = state.timeOfDay,
                measuredAt = LocalDateTime.of(state.date, state.time),
                note = state.note.ifBlank { null },
                status = ReadingStatus.NORMAL
            )
            if (state.isEditing) {
                repository.updateReading(reading)
            } else {
                repository.addReading(reading)
            }
        }
    }
}

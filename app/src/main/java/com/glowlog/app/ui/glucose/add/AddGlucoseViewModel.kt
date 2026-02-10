package com.glowlog.app.ui.glucose.add

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.R
import com.glowlog.app.data.repository.GlucoseRepository
import com.glowlog.app.domain.model.GlucoseReading
import com.glowlog.app.domain.model.MealContext
import com.glowlog.app.domain.model.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

data class AddGlucoseUiState(
    val value: String = "",
    val mealContext: MealContext = MealContext.FASTING,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val note: String = "",
    val isEditing: Boolean = false,
    val editingId: String? = null,
    val isSaved: Boolean = false,
    @StringRes val valueError: Int? = null
)

@HiltViewModel
class AddGlucoseViewModel @Inject constructor(
    private val repository: GlucoseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGlucoseUiState())
    val uiState: StateFlow<AddGlucoseUiState> = _uiState.asStateFlow()

    init {
        val readingId = savedStateHandle.get<String>("readingId")
        if (readingId != null) {
            loadReading(readingId)
        }
    }

    fun loadReading(readingId: String) {
        viewModelScope.launch {
            val reading = repository.getReadingById(readingId) ?: return@launch
            _uiState.value = AddGlucoseUiState(
                value = reading.valueMmol.toString(),
                mealContext = reading.mealContext,
                date = reading.measuredAt.toLocalDate(),
                time = reading.measuredAt.toLocalTime(),
                note = reading.note ?: "",
                isEditing = true,
                editingId = reading.id
            )
        }
    }

    fun onValueChange(value: String) {
        _uiState.value = _uiState.value.copy(value = value, valueError = null)
    }

    fun onMealContextChange(mealContext: MealContext) {
        _uiState.value = _uiState.value.copy(mealContext = mealContext)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onTimeChange(time: LocalTime) {
        _uiState.value = _uiState.value.copy(time = time)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaved) return

        val valueMmol = state.value.replace(",", ".").toDoubleOrNull()
        if (valueMmol == null || valueMmol <= 0 || valueMmol > 40) {
            _uiState.value = state.copy(valueError = R.string.error_glucose_value)
            return
        }

        _uiState.value = state.copy(isSaved = true)
        viewModelScope.launch {
            val reading = GlucoseReading(
                id = state.editingId ?: UUID.randomUUID().toString(),
                valueMmol = valueMmol,
                mealContext = state.mealContext,
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

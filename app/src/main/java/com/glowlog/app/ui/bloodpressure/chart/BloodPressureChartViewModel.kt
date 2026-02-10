package com.glowlog.app.ui.bloodpressure.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.repository.BloodPressureRepository
import com.glowlog.app.domain.model.BloodPressureReading
import com.glowlog.app.domain.model.DateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BloodPressureChartViewModel @Inject constructor(
    private val repository: BloodPressureRepository
) : ViewModel() {

    private val _dateRange = MutableStateFlow<DateRange>(DateRange.Week)
    val dateRange: StateFlow<DateRange> = _dateRange.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val readings: StateFlow<List<BloodPressureReading>> = _dateRange.flatMapLatest { range ->
        val (start, end) = range.toEpochMillis()
        repository.getReadingsByDateRangeAsc(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDateRangeChange(range: DateRange) {
        _dateRange.value = range
    }
}

package com.glowlog.app.ui.glucose.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.repository.GlucoseRepository
import com.glowlog.app.domain.model.DateRange
import com.glowlog.app.domain.model.GlucoseReading
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
class GlucoseChartViewModel @Inject constructor(
    private val repository: GlucoseRepository
) : ViewModel() {

    private val _dateRange = MutableStateFlow<DateRange>(DateRange.Week)
    val dateRange: StateFlow<DateRange> = _dateRange.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val readings: StateFlow<List<GlucoseReading>> = _dateRange.flatMapLatest { range ->
        val (start, end) = range.toEpochMillis()
        repository.getReadingsByDateRangeAsc(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDateRangeChange(range: DateRange) {
        _dateRange.value = range
    }
}

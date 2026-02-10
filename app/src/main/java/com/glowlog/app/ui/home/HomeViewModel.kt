package com.glowlog.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.repository.BloodPressureRepository
import com.glowlog.app.data.repository.GlucoseRepository
import com.glowlog.app.domain.model.BloodPressureReading
import com.glowlog.app.domain.model.GlucoseReading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    glucoseRepository: GlucoseRepository,
    bloodPressureRepository: BloodPressureRepository
) : ViewModel() {

    val recentGlucose: StateFlow<List<GlucoseReading>> = glucoseRepository
        .getRecentReadings(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentBloodPressure: StateFlow<List<BloodPressureReading>> = bloodPressureRepository
        .getRecentReadings(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

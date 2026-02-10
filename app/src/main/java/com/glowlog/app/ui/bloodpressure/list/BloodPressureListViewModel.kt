package com.glowlog.app.ui.bloodpressure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.repository.BloodPressureRepository
import com.glowlog.app.domain.model.BloodPressureReading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BloodPressureListViewModel @Inject constructor(
    private val repository: BloodPressureRepository
) : ViewModel() {

    val readings: StateFlow<List<BloodPressureReading>> = repository
        .getAllReadings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteReading(id: String) {
        viewModelScope.launch {
            repository.deleteReading(id)
        }
    }
}

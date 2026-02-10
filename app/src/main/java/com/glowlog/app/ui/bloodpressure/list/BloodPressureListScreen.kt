package com.glowlog.app.ui.bloodpressure.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glowlog.app.R
import com.glowlog.app.ui.common.components.EmptyState
import com.glowlog.app.ui.common.components.ReadingCard
import com.glowlog.app.ui.common.util.DateTimeFormatters

@Composable
fun BloodPressureListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: BloodPressureListViewModel = hiltViewModel()
) {
    val readings by viewModel.readings.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_add_entry))
            }
        }
    ) { innerPadding ->
        if (readings.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.empty_bp_title),
                subtitle = stringResource(R.string.empty_bp_subtitle),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(readings, key = { it.id }) { reading ->
                    val pulseText = reading.pulse?.let { stringResource(R.string.label_pulse, it) } ?: ""
                    ReadingCard(
                        title = stringResource(R.string.nav_pressure),
                        value = "${reading.systolic}/${reading.diastolic}$pulseText",
                        subtitle = "${stringResource(reading.timeOfDay.labelRes)}, ${stringResource(R.string.label_arm_hand, stringResource(reading.arm.labelRes))}",
                        dateTime = DateTimeFormatters.formatDateTime(reading.measuredAt),
                        status = reading.status,
                        onClick = { onNavigateToEdit(reading.id) },
                        note = reading.note
                    )
                }
            }
        }
    }
}

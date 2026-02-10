package com.glowlog.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glowlog.app.R
import com.glowlog.app.ui.common.components.ReadingCard
import com.glowlog.app.ui.common.util.DateTimeFormatters

@Composable
fun HomeScreen(
    onNavigateToAddGlucose: () -> Unit,
    onNavigateToAddBloodPressure: () -> Unit,
    onNavigateToGlucoseList: () -> Unit,
    onNavigateToBloodPressureList: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentGlucose by viewModel.recentGlucose.collectAsStateWithLifecycle()
    val recentBloodPressure by viewModel.recentBloodPressure.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Quick add buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToAddGlucose,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text(stringResource(R.string.nav_glucose))
            }
            OutlinedButton(
                onClick = onNavigateToAddBloodPressure,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text(stringResource(R.string.nav_pressure))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent glucose readings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.nav_glucose), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onNavigateToGlucoseList) {
                Text(stringResource(R.string.label_all_entries))
            }
        }

        if (recentGlucose.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_no_entries),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            recentGlucose.forEach { reading ->
                ReadingCard(
                    title = stringResource(R.string.nav_glucose),
                    value = "${reading.valueMmol} ${stringResource(R.string.glucose_unit)}",
                    subtitle = stringResource(reading.mealContext.labelRes),
                    dateTime = DateTimeFormatters.formatDateTime(reading.measuredAt),
                    status = reading.status,
                    onClick = {},
                    note = reading.note,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent blood pressure readings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.nav_pressure), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onNavigateToBloodPressureList) {
                Text(stringResource(R.string.label_all_entries))
            }
        }

        if (recentBloodPressure.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_no_entries),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            recentBloodPressure.forEach { reading ->
                val pulseText = reading.pulse?.let { stringResource(R.string.label_pulse, it) } ?: ""
                ReadingCard(
                    title = stringResource(R.string.nav_pressure),
                    value = "${reading.systolic}/${reading.diastolic}$pulseText",
                    subtitle = "${stringResource(reading.timeOfDay.labelRes)}, ${stringResource(R.string.label_arm_hand, stringResource(reading.arm.labelRes))}",
                    dateTime = DateTimeFormatters.formatDateTime(reading.measuredAt),
                    status = reading.status,
                    onClick = {},
                    note = reading.note,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

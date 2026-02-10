package com.glowlog.app.ui.bloodpressure.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glowlog.app.R
import com.glowlog.app.domain.model.Arm
import com.glowlog.app.ui.common.components.GlowDatePickerDialog
import com.glowlog.app.ui.common.components.GlowTimePickerDialog
import com.glowlog.app.ui.common.util.DateTimeFormatters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBloodPressureScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddBloodPressureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.systolic,
                onValueChange = viewModel::onSystolicChange,
                label = { Text(stringResource(R.string.label_systolic)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.systolicError != null,
                supportingText = uiState.systolicError?.let { { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.diastolic,
                onValueChange = viewModel::onDiastolicChange,
                label = { Text(stringResource(R.string.label_diastolic)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.diastolicError != null,
                supportingText = uiState.diastolicError?.let { { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.pulse,
            onValueChange = viewModel::onPulseChange,
            label = { Text(stringResource(R.string.label_pulse_optional)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Arm selection
        Text(stringResource(R.string.label_arm), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Arm.entries.forEach { arm ->
                FilterChip(
                    selected = uiState.arm == arm,
                    onClick = { viewModel.onArmChange(arm) },
                    label = { Text(stringResource(arm.labelRes)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date & Time with auto-detected time of day badge
        Text(stringResource(R.string.label_date_time), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { showDatePicker = true }) {
                Text(DateTimeFormatters.formatDate(uiState.date))
            }
            TextButton(onClick = { showTimePicker = true }) {
                Text(uiState.time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
            }
            androidx.compose.material3.AssistChip(
                onClick = {},
                label = { Text(stringResource(uiState.timeOfDay.labelRes)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.note,
            onValueChange = viewModel::onNoteChange,
            label = { Text(stringResource(R.string.label_complaints_optional)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::save,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.isEditing) stringResource(R.string.btn_save) else stringResource(R.string.btn_add))
        }
    }

    if (showDatePicker) {
        GlowDatePickerDialog(
            currentDate = uiState.date,
            onDateSelected = viewModel::onDateChange,
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        GlowTimePickerDialog(
            currentTime = uiState.time,
            onTimeSelected = viewModel::onTimeChange,
            onDismiss = { showTimePicker = false }
        )
    }
}

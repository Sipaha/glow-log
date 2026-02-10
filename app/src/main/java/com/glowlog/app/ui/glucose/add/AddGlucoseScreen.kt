package com.glowlog.app.ui.glucose.add

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glowlog.app.R
import com.glowlog.app.domain.model.MealContext
import com.glowlog.app.ui.common.components.GlowDatePickerDialog
import com.glowlog.app.ui.common.components.GlowTimePickerDialog
import com.glowlog.app.ui.common.util.DateTimeFormatters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddGlucoseScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddGlucoseViewModel = hiltViewModel()
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
        OutlinedTextField(
            value = uiState.value,
            onValueChange = viewModel::onValueChange,
            label = { Text(stringResource(R.string.label_glucose_mmol)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = uiState.valueError != null,
            supportingText = uiState.valueError?.let { { Text(stringResource(it)) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.label_meal_context), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MealContext.entries.forEach { ctx ->
                FilterChip(
                    selected = uiState.mealContext == ctx,
                    onClick = { viewModel.onMealContextChange(ctx) },
                    label = { Text(stringResource(ctx.labelRes)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date & Time
        Text(stringResource(R.string.label_date_time), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { showDatePicker = true }) {
                Text(DateTimeFormatters.formatDate(uiState.date))
            }
            TextButton(onClick = { showTimePicker = true }) {
                Text(uiState.time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.note,
            onValueChange = viewModel::onNoteChange,
            label = { Text(stringResource(R.string.label_note_optional)) },
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

package com.glowlog.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glowlog.app.R

@Composable
fun SettingsScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()
    val todRanges by viewModel.timeOfDayRanges.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.shareEvent.collect { event ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, event.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_export, event.fileName)))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Account section
        Text(stringResource(R.string.section_account), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (currentUser != null) {
            Text(
                text = currentUser?.displayName ?: stringResource(R.string.label_user),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = currentUser?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { viewModel.signOut() }) {
                Text(stringResource(R.string.btn_sign_out))
            }
        } else {
            Text(
                text = stringResource(R.string.label_sign_in_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToSignIn) {
                Text(stringResource(R.string.btn_sign_in_google))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Reminders section
        Text(stringResource(R.string.section_reminders), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ReminderToggle(
            label = stringResource(R.string.reminder_morning, reminderSettings.morningTime),
            checked = reminderSettings.morningEnabled,
            onCheckedChange = {
                viewModel.updateReminderSettings(reminderSettings.copy(morningEnabled = it))
            }
        )

        ReminderToggle(
            label = stringResource(R.string.reminder_evening, reminderSettings.eveningTime),
            checked = reminderSettings.eveningEnabled,
            onCheckedChange = {
                viewModel.updateReminderSettings(reminderSettings.copy(eveningEnabled = it))
            }
        )

        ReminderToggle(
            label = stringResource(R.string.reminder_after_meal),
            checked = reminderSettings.afterMealEnabled,
            onCheckedChange = {
                viewModel.updateReminderSettings(reminderSettings.copy(afterMealEnabled = it))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Time of day ranges section
        Text(stringResource(R.string.section_tod_ranges), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.tod_ranges_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeOfDayHourField(
                label = stringResource(R.string.tod_morning),
                value = todRanges.morningStart,
                onValueChange = { viewModel.updateTimeOfDayRanges(todRanges.copy(morningStart = it)) },
                modifier = Modifier.weight(1f)
            )
            TimeOfDayHourField(
                label = stringResource(R.string.tod_day),
                value = todRanges.dayStart,
                onValueChange = { viewModel.updateTimeOfDayRanges(todRanges.copy(dayStart = it)) },
                modifier = Modifier.weight(1f)
            )
            TimeOfDayHourField(
                label = stringResource(R.string.tod_evening),
                value = todRanges.eveningStart,
                onValueChange = { viewModel.updateTimeOfDayRanges(todRanges.copy(eveningStart = it)) },
                modifier = Modifier.weight(1f)
            )
            TimeOfDayHourField(
                label = stringResource(R.string.tod_night),
                value = todRanges.nightStart,
                onValueChange = { viewModel.updateTimeOfDayRanges(todRanges.copy(nightStart = it)) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Export section
        Text(stringResource(R.string.section_export), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.exportGlucoseCsv() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_export_glucose))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.exportBloodPressureCsv() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_export_bp))
        }
    }
}

@Composable
private fun ReminderToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun TimeOfDayHourField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { text ->
            val hour = text.toIntOrNull()
            if (hour != null && hour in 0..23) {
                onValueChange(hour)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier
    )
}

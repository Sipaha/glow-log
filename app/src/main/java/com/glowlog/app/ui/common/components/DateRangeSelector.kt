package com.glowlog.app.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glowlog.app.R
import com.glowlog.app.domain.model.DateRange

@Composable
fun DateRangeSelector(
    selected: DateRange,
    onSelect: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected is DateRange.Week,
            onClick = { onSelect(DateRange.Week) },
            label = { Text(stringResource(R.string.range_week)) }
        )
        FilterChip(
            selected = selected is DateRange.Month,
            onClick = { onSelect(DateRange.Month) },
            label = { Text(stringResource(R.string.range_month)) }
        )
    }
}

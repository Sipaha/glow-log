package com.glowlog.app.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowlog.app.domain.model.ReadingStatus
import com.glowlog.app.ui.theme.StatusBorderline
import com.glowlog.app.ui.theme.StatusHigh
import com.glowlog.app.ui.theme.StatusNormal

@Composable
fun StatusBadge(status: ReadingStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        ReadingStatus.NORMAL -> StatusNormal.copy(alpha = 0.15f) to StatusNormal
        ReadingStatus.BORDERLINE -> StatusBorderline.copy(alpha = 0.15f) to StatusBorderline
        ReadingStatus.HIGH -> StatusHigh.copy(alpha = 0.15f) to StatusHigh
    }

    Text(
        text = stringResource(status.labelRes),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

fun statusColor(status: ReadingStatus): Color = when (status) {
    ReadingStatus.NORMAL -> StatusNormal
    ReadingStatus.BORDERLINE -> StatusBorderline
    ReadingStatus.HIGH -> StatusHigh
}

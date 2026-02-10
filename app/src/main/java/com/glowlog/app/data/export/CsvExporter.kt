package com.glowlog.app.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.glowlog.app.R
import com.glowlog.app.data.local.db.dao.BloodPressureReadingDao
import com.glowlog.app.data.local.db.dao.GlucoseReadingDao
import com.glowlog.app.data.repository.toDomain
import com.glowlog.app.domain.model.Arm
import com.glowlog.app.domain.model.BloodPressureReading
import com.glowlog.app.domain.model.TimeOfDay
import com.glowlog.app.ui.common.util.DateTimeFormatters
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val glucoseDao: GlucoseReadingDao,
    private val bloodPressureDao: BloodPressureReadingDao
) {
    /**
     * Generates glucose CSV file and returns a content URI for sharing.
     */
    suspend fun exportGlucoseCsv(): Uri {
        val readings = glucoseDao.getAll().map { it.toDomain() }

        val timestamp = exportTimestamp()
        val csv = buildString {
            appendLine(appContext.getString(R.string.csv_glucose_header))
            readings.forEach { r ->
                appendLine(
                    "${escapeCsv(DateTimeFormatters.formatDate(r.measuredAt))}," +
                    "${escapeCsv(DateTimeFormatters.formatTime(r.measuredAt))}," +
                    "${r.valueMmol}," +
                    "${escapeCsv(appContext.getString(r.mealContext.labelRes))}," +
                    "${escapeCsv(appContext.getString(r.status.labelRes))}," +
                    escapeCsv(r.note)
                )
            }
        }

        return writeAndGetUri("glucose_$timestamp.csv", csv)
    }

    /**
     * Generates blood pressure CSV file (pivot table grouped by date) and returns a content URI.
     */
    suspend fun exportBloodPressureCsv(): Uri {
        val readings = bloodPressureDao.getAll().map { it.toDomain() }

        val byDate: Map<LocalDate, List<BloodPressureReading>> = readings
            .groupBy { it.measuredAt.toLocalDate() }
            .toSortedMap()

        val timestamp = exportTimestamp()
        val csv = buildString {
            appendLine(appContext.getString(R.string.csv_bp_header))

            byDate.forEach { (date, dayReadings) ->
                val dateStr = DateTimeFormatters.formatDate(date)

                fun findReading(tod: TimeOfDay, arm: Arm): BloodPressureReading? =
                    dayReadings.find { it.timeOfDay == tod && it.arm == arm }

                fun formatBp(reading: BloodPressureReading?): String =
                    reading?.let { "${it.systolic}/${it.diastolic}" } ?: ""

                val morningRight = findReading(TimeOfDay.MORNING, Arm.RIGHT)
                val morningLeft = findReading(TimeOfDay.MORNING, Arm.LEFT)
                val dayRight = findReading(TimeOfDay.DAY, Arm.RIGHT)
                val dayLeft = findReading(TimeOfDay.DAY, Arm.LEFT)
                val eveningRight = findReading(TimeOfDay.EVENING, Arm.RIGHT)
                val eveningLeft = findReading(TimeOfDay.EVENING, Arm.LEFT)
                val nightRight = findReading(TimeOfDay.NIGHT, Arm.RIGHT)
                val nightLeft = findReading(TimeOfDay.NIGHT, Arm.LEFT)

                val pulse = dayReadings.firstNotNullOfOrNull { it.pulse }?.toString() ?: ""
                val notes = dayReadings.mapNotNull { it.note }.distinct().joinToString("; ")

                appendLine(
                    "${escapeCsv(dateStr)}," +
                    "${escapeCsv(formatBp(morningRight))}," +
                    "${escapeCsv(formatBp(morningLeft))}," +
                    "${escapeCsv(formatBp(dayRight))}," +
                    "${escapeCsv(formatBp(dayLeft))}," +
                    "${escapeCsv(formatBp(eveningRight))}," +
                    "${escapeCsv(formatBp(eveningLeft))}," +
                    "${escapeCsv(formatBp(nightRight))}," +
                    "${escapeCsv(formatBp(nightLeft))}," +
                    "${escapeCsv(pulse)}," +
                    escapeCsv(notes)
                )
            }
        }

        return writeAndGetUri("blood_pressure_$timestamp.csv", csv)
    }

    private fun exportTimestamp(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))

    private fun escapeCsv(value: String?): String {
        if (value.isNullOrBlank()) return ""
        return "\"${value.replace("\"", "\"\"")}\""
    }

    private fun writeAndGetUri(fileName: String, content: String): Uri {
        val exportDir = File(appContext.cacheDir, "exports")
        exportDir.mkdirs()
        val file = File(exportDir, fileName)
        file.writeText(content)

        return FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
    }
}

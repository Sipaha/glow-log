package com.glowlog.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.glowlog.app.data.local.db.dao.BloodPressureReadingDao
import com.glowlog.app.data.local.db.dao.GlucoseReadingDao
import com.glowlog.app.data.local.db.entity.BloodPressureReadingEntity
import com.glowlog.app.data.local.db.entity.GlucoseReadingEntity

@Database(
    entities = [
        GlucoseReadingEntity::class,
        BloodPressureReadingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GlowLogDatabase : RoomDatabase() {
    abstract fun glucoseReadingDao(): GlucoseReadingDao
    abstract fun bloodPressureReadingDao(): BloodPressureReadingDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE blood_pressure_readings ADD COLUMN arm TEXT NOT NULL DEFAULT 'LEFT'")
                db.execSQL("ALTER TABLE blood_pressure_readings ADD COLUMN timeOfDay TEXT NOT NULL DEFAULT 'MORNING'")
            }
        }
    }
}

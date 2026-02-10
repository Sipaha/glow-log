package com.glowlog.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.glowlog.app.data.local.datastore.UserPreferences
import com.glowlog.app.data.local.datastore.dataStore
import com.glowlog.app.data.local.db.GlowLogDatabase
import com.glowlog.app.data.local.db.dao.BloodPressureReadingDao
import com.glowlog.app.data.local.db.dao.GlucoseReadingDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GlowLogDatabase {
        return Room.databaseBuilder(
            context,
            GlowLogDatabase::class.java,
            "glowlog.db"
        ).addMigrations(GlowLogDatabase.MIGRATION_1_2).build()
    }

    @Provides
    fun provideGlucoseReadingDao(database: GlowLogDatabase): GlucoseReadingDao {
        return database.glucoseReadingDao()
    }

    @Provides
    fun provideBloodPressureReadingDao(database: GlowLogDatabase): BloodPressureReadingDao {
        return database.bloodPressureReadingDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}

package com.glowlog.app.di

import com.glowlog.app.data.repository.AuthRepository
import com.glowlog.app.data.repository.AuthRepositoryImpl
import com.glowlog.app.data.repository.BloodPressureRepository
import com.glowlog.app.data.repository.BloodPressureRepositoryImpl
import com.glowlog.app.data.repository.GlucoseRepository
import com.glowlog.app.data.repository.GlucoseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGlucoseRepository(impl: GlucoseRepositoryImpl): GlucoseRepository

    @Binds
    @Singleton
    abstract fun bindBloodPressureRepository(impl: BloodPressureRepositoryImpl): BloodPressureRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

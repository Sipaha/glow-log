package com.glowlog.app

import android.app.Application
import androidx.work.Configuration
import com.glowlog.app.data.sync.SyncManager
import com.glowlog.app.reminder.ReminderNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.hilt.work.HiltWorkerFactory

@HiltAndroidApp
class GlowLogApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncManager: SyncManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        ReminderNotificationHelper.createChannel(this)
        syncManager.schedulePeriodicSync()
    }
}

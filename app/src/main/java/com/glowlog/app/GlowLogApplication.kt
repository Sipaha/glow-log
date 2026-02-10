package com.glowlog.app

import android.app.Application
import com.glowlog.app.data.sync.SyncManager
import com.glowlog.app.reminder.ReminderNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GlowLogApplication : Application() {

    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()
        ReminderNotificationHelper.createChannel(this)
        syncManager.schedulePeriodicSync()
    }
}

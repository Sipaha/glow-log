package com.glowlog.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.glowlog.app.R
import com.glowlog.app.data.local.datastore.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(ReminderScheduler.EXTRA_REMINDER_TYPE)

        val (title, text, notificationId) = when (type) {
            ReminderScheduler.TYPE_MORNING -> Triple(
                context.getString(R.string.notification_morning_title),
                context.getString(R.string.notification_morning_text),
                1
            )
            ReminderScheduler.TYPE_EVENING -> Triple(
                context.getString(R.string.notification_evening_title),
                context.getString(R.string.notification_evening_text),
                2
            )
            else -> return
        }

        ReminderNotificationHelper.showNotification(context, title, text, notificationId)

        // Reschedule for next day
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = userPreferences.reminderSettings.first()
                reminderScheduler.scheduleReminders(settings)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

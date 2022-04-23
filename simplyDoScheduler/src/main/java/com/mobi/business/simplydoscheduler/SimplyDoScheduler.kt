package com.mobi.business.simplydoscheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mobi.business.simplydoscheduler.Receiver.SchedulerReceiverActivity

class SimplyDoScheduler(private val context: Context) {

    companion object {
        fun getInstance(context: Context): SimplyDoScheduler {
            return SimplyDoScheduler(context)
        }
    }

    public fun scheduleNotification(
        notificationId: Int
    ) {
        // Get AlarmManager instance
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, SchedulerReceiverActivity::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium AlarmManager Demo")

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        // Alarm time
        val ALARM_DELAY_IN_SECOND = 10
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L

        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTimeAtUTC,
            pendingIntent
        )

    }

    public fun scheduleRepeatedNotification() {

    }
}
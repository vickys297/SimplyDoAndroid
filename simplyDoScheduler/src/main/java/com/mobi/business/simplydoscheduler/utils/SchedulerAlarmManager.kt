package com.mobi.business.simplydoscheduler.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mobi.business.simplydoscheduler.model.NotificationDataModel
import com.mobi.business.simplydoscheduler.receiver.SchedulerReceiver
import java.util.*

class SchedulerAlarmManager(val context: Context) {

    companion object {
        fun getInstance(context: Context): SchedulerAlarmManager {
            return SchedulerAlarmManager(context)
        }
    }

    fun setOneTimeAlarm(notificationId: Int, timeInMillis: Long) {
        // Get SchedulerAlarmManager instance
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, SchedulerReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium SchedulerAlarmManager Demo")

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    fun scheduleAnAlarm(notification: NotificationDataModel) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, SchedulerReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium SchedulerAlarmManager Demo")

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notification.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        for (days in notification.repeatable) {
            val alarmTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, notification.dateTime.time.hours)
                set(Calendar.MINUTE, notification.dateTime.time.minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, days)
            }.timeInMillis

            val aMinute = 60000L
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                aMinute,
                pendingIntent
            )
        }

    }

}
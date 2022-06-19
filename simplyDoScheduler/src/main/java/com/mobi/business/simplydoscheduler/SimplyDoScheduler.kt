package com.mobi.business.simplydoscheduler

import android.content.Context
import android.util.Log
import com.mobi.business.simplydoscheduler.model.NotificationDataModel
import com.mobi.business.simplydoscheduler.utils.SchedulerAlarmManager
import java.util.*

internal val TAG = SimplyDoScheduler::class.java.simpleName

class SimplyDoScheduler(private val context: Context, val alarmManager: SchedulerAlarmManager) {

    companion object {
        fun getInstance(context: Context): SimplyDoScheduler {
            val alarmManager = SchedulerAlarmManager.getInstance(context)
            return SimplyDoScheduler(context, alarmManager)
        }
    }


    fun scheduleNotification(
        notification: NotificationDataModel
    ) {
        Log.i(TAG, "scheduleNotification: $notification")
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, notification.dateTime.time.hours)
            set(Calendar.MINUTE, notification.dateTime.time.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.DAY_OF_MONTH, notification.dateTime.date.day)
            set(Calendar.MONTH, notification.dateTime.date.month)
            set(Calendar.YEAR, notification.dateTime.date.year)
        }.timeInMillis


        val timeInMilliSec = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        if (notificationTime <= timeInMilliSec) {
            alarmManager.setOneTimeAlarm(
                notificationId = notification.id,
                timeInMillis = notificationTime
            )
        } else {
            alarmManager.scheduleAnAlarm(notification)
        }
    }

    fun scheduleRepeatedNotification(notification: NotificationDataModel) {
        Log.i(TAG, "scheduleRepeatedNotification: $notification.id")
        alarmManager.scheduleAnAlarm(notification)
    }


    fun cancelScheduledTask(notificationId: Int) {

    }
}
package com.example.simplydo.utli

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simplydo.R
import com.example.simplydo.receiver.CloseNotificationReceiver
import com.example.simplydo.ui.MainActivity


fun NotificationManager.sendNotification(
    notificationId: Long,
    title: String,
    task: String,
    priority: Boolean,
    applicationContext: Context,
    type: Int = AppConstant.NOTIFICATION_TASK_NEW
) {
    val channelId = "task_notification_$notificationId"

    // 3. Set up main Intent for notification.
    val notifyIntent = Intent(applicationContext, MainActivity::class.java).apply {
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // When creating your Intent, you need to take into account the back state, i.e., what
    // happens after your Activity launches and the user presses the back button.

    // There are two options:
    //      1. Regular activity - You're starting an Activity that's part of the application's
    //      normal workflow.

    //      2. Special activity - The user only sees this Activity if it's started from a
    //      notification. In a sense, the Activity extends the notification by providing
    //      information that would be hard to display in the notification itself.

    // For the BIG_TEXT_STYLE notification, we will consider the activity launched by the main
    // Intent as a special activity, so we will follow option 2.

    // For an example of option 1, check either the MESSAGING_STYLE or BIG_PICTURE_STYLE
    // examples.

    // For more information, check out our dev article:
    // https://developer.android.com/training/notify-user/navigation.html

    // Sets the Activity to start in a new, empty task

    val notifyPendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        notifyIntent,
        0
    )


    // 4. Create additional Actions (Intents) for the Notification.

    // In our case, we create two additional actions: a Snooze action and a Dismiss action.
    // Snooze Action.


    val completeTaskIntent =
        Intent(applicationContext, CloseNotificationReceiver::class.java).apply {
            action = AppConstant.ACTION_COMPLETE
            putExtra(AppConstant.NAVIGATION_TASK_KEY, notificationId)
        }

    val completeTaskPendingIntent =
        PendingIntent.getBroadcast(
            applicationContext,
            notificationId.toInt(),
            completeTaskIntent,
            0
        )

    // Dismiss Action.
    val viewTaskIntent =
        Intent(applicationContext, CloseNotificationReceiver::class.java).apply {
            action = AppConstant.ACTION_VIEW
            putExtra(AppConstant.NAVIGATION_TASK_KEY, notificationId)
        }
    val viewTaskPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        notificationId.toInt(),
        viewTaskIntent,
        0
    )

    // View List Action.
    val viewListTask =
        Intent(applicationContext, CloseNotificationReceiver::class.java).apply {
            action = AppConstant.ACTION_VIEW_MY_TASK
        }
    val viewTodayTaskPendingList = PendingIntent.getBroadcast(
        applicationContext,
        notificationId.toInt(),
        viewListTask,
        0
    )


    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notification Name"
        val descriptionText = "Notification Description"

        val importance = when (type) {
            AppConstant.NOTIFICATION_TASK_NEW -> {
                NotificationManager.IMPORTANCE_HIGH
            }
            AppConstant.NOTIFICATION_TASK_DAILY_REMAINDER -> {
                NotificationManager.IMPORTANCE_LOW
            }
            else -> {
                NotificationManager.IMPORTANCE_DEFAULT
            }
        }

        val channel = NotificationChannel(
            channelId,
            name,
            importance
        ).apply {
            description = descriptionText
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        channelId
    )
        .setSmallIcon(R.drawable.app_icon)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .setSummaryText(task)
        )
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(notifyPendingIntent)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setAutoCancel(true)

    when (type) {
        AppConstant.NOTIFICATION_TASK_NEW -> {
            Log.i(TAG, "sendNotification: 1")
            builder.setContentTitle(title)
                .setContentText(task)
                .addAction(
                    R.drawable.ic_baseline_close_24_primary,
                    applicationContext.getString(R.string.view_task),
                    viewTaskPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_check_24_primary,
                    applicationContext.getString(R.string.complete_task),
                    completeTaskPendingIntent
                )
                .priority = NotificationCompat.PRIORITY_MAX
        }
        AppConstant.NOTIFICATION_TASK_DAILY_REMAINDER -> {
            Log.i(TAG, "sendNotification: 2")
            builder.setContentTitle(title)
                .setContentText(task)
                .addAction(
                    R.drawable.ic_baseline_close_24_primary,
                    applicationContext.getString(R.string.view_my_task),
                    viewTodayTaskPendingList
                ).priority = NotificationCompat.PRIORITY_DEFAULT
        }
        else -> {

        }
    }

    if (priority) {
        builder.setSubText("Is High Priority")
    }

    with(NotificationManagerCompat.from(applicationContext)) {
        notify(notificationId.toInt(), builder.build())
    }
}

// TODO: Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

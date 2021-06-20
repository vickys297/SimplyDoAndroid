package com.example.simplydo.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.ui.MainActivity
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppRepository


internal val closeNotificationReceiver = CloseNotificationReceiver::class.java.canonicalName

class CloseNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i(closeNotificationReceiver, "onReceive: ")


        when (intent.action) {
            AppConstant.ACTION_VIEW -> {
                Log.i(TAG, "onReceive: ACTION_VIEW")
                cancelNotification(intent, context)

                val notificationID = intent.getLongExtra(AppConstant.NAVIGATION_TASK_KEY, 0L)
                Log.i(TAG, "onReceive: $notificationID")

                val bundle = Bundle()
                bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, notificationID)


                Log.i(TAG, "onReceive: ${bundle.getLong(AppConstant.NAVIGATION_TASK_KEY)}")

                val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(AppConstant.NAVIGATION_TASK_KEY, notificationID)
                }
                context.startActivity(mainActivityIntent)

            }
            AppConstant.ACTION_COMPLETE -> {
                Log.i(TAG, "onReceive: ACTION_COMPLETE")
                cancelNotification(intent, context)
                updateTaskCompleted(intent, context)
            }
            AppConstant.ACTION_VIEW_MY_TASK->{

                Log.i(TAG, "onReceive: ACTION_VIEW_MY_TASK")
                cancelNotification(intent, context)

                val notificationID = intent.getLongExtra(AppConstant.NAVIGATION_TASK_KEY, 0L)
                val bundle = Bundle()
                bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, notificationID)
                val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(AppConstant.NAVIGATION_TASK_KEY, notificationID)
                }

                context.startActivity(mainActivityIntent)
            }
        }
    }

    private fun updateTaskCompleted(intent: Intent, context: Context) {
        intent.getLongExtra(AppConstant.NAVIGATION_TASK_KEY, 0L).let {
            if (it != 0L) {
                val appRepository =
                    AppRepository.getInstance(context = context, AppDatabase.getInstance(context))
                appRepository.completeTaskById(it)
            }
        }

    }

    private fun cancelNotification(intent: Intent, context: Context) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        intent.getLongExtra(AppConstant.NAVIGATION_TASK_KEY, 0L).let {
            if (it == 0L) {
                notificationManager.cancelAll()
            } else {
                notificationManager.cancel(it.toInt())
            }
        }
    }
}
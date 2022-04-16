package com.example.simplydo.workerClass

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.sendNotification

internal val TAG = RegularNotificationWorker::class.java.canonicalName

class RegularNotificationWorker(val appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        Log.i(TAG, "doWork: started")
        val appDatabase = AppDatabase.getInstance(appContext)
        val todo = appDatabase.todoDao()

        val taskAvailable = todo.getSingleDayTodoListTotalCount(
            AppFunctions.getCurrentDayStartInMilliSeconds(),
            AppFunctions.getCurrentDayEndInMilliSeconds()
        )

        if (taskAvailable > 0) {
            val notificationManager = ContextCompat.getSystemService(
                appContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                notificationId = 0,
                title = "Gentle task remainder",
                task = "You have $taskAvailable tasks today.",
                priority = false,
                applicationContext = appContext,
                type = AppConstant.NOTIFICATION_TASK_DAILY_REMAINDER,
                workspace = false
            )

        }

        return Result.success()
    }
}
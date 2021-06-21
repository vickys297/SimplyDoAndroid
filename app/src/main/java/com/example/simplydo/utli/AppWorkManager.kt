package com.example.simplydo.utli

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.simplydo.TAG_MY
import com.example.simplydo.workerClass.RegularNotificationWorker
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

internal const val REGULAR_NOTIFICATION_TAG = "regular_notification_tag"

class AppWorkManager(val context: Context) {

    fun startWorker() {
        if (isWorkScheduled(REGULAR_NOTIFICATION_TAG) != WorkInfo.State.ENQUEUED &&
            isWorkScheduled(REGULAR_NOTIFICATION_TAG) != WorkInfo.State.RUNNING
        ) {
            createWorkRequest()
            Log.i(TAG_MY, ": server started")
        } else {
            Log.i(TAG_MY, ": server already working")


        }
    }

    private fun isWorkScheduled(tag: String): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(context).getWorkInfosForUniqueWork(tag)
                    .get().size > 0
            ) {
                WorkManager.getInstance(context).getWorkInfosForUniqueWork(tag)
                    .get()[0].state
                // this can return WorkInfo.State.ENQUEUED or WorkInfo.State.RUNNING
                // you can check all of them in WorkInfo class.
            } else {
                WorkInfo.State.CANCELLED
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        } catch (e: InterruptedException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        }
    }

    private fun createWorkRequest() {
        val regularNotificationWorker = PeriodicWorkRequest
            .Builder(
                RegularNotificationWorker::class.java,
                1, TimeUnit.DAYS,
                6,TimeUnit.HOURS
            )
            .build()

        val instance = WorkManager.getInstance(context)
        instance.enqueueUniquePeriodicWork(
            REGULAR_NOTIFICATION_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            regularNotificationWorker
        )
    }

    companion object {
        fun getInstance(context: Context) = AppWorkManager(context)
    }

}
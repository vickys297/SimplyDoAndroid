package com.example.simplydo.workerClass

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

internal val TAG = RegularNotificationWorker::class.java.canonicalName

class RegularNotificationWorker(val appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        Log.i(TAG, "doWork: started")

        return Result.success()
    }
}
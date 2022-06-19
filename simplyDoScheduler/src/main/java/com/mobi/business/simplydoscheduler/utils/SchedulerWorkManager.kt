package com.mobi.business.simplydoscheduler.utils

import androidx.work.WorkManager
import androidx.work.WorkRequest

class SchedulerWorkManager {

    companion object{
        fun getInstance(): SchedulerWorkManager {
            return SchedulerWorkManager()
        }
    }


   fun createOneTimeWorkRequest(workRequest: WorkRequest) {
        WorkManager.getInstance().enqueue(workRequest)
    }

    fun createPeriodicWorkRequest(workRequest: WorkRequest) {
        WorkManager.getInstance().enqueue(workRequest)
    }

    fun cancelWorkRequest(workRequest: WorkRequest) {
        WorkManager.getInstance().cancelWorkById(workRequest.id)
    }

    fun cancelAllWorkRequest() {
        WorkManager.getInstance().cancelAllWork()
    }

    fun getWorkRequestById(workRequest: WorkRequest) {
        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.id)
    }

    fun getAllWorkRequest() {
        WorkManager.getInstance().getWorkInfosByTagLiveData("")
    }
}
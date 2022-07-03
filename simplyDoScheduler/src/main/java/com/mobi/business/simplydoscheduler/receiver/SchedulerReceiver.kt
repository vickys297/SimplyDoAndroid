package com.mobi.business.simplydoscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log

internal val TAG = SchedulerReceiver::class.java.simpleName

class SchedulerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: android.content.Intent) {
        Log.i(TAG, "onReceive: ")
    }
}
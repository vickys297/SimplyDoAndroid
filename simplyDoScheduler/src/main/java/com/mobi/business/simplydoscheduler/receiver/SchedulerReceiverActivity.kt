package com.mobi.business.simplydoscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log

internal val TAG = SchedulerReceiverActivity::class.java.simpleName

class SchedulerReceiverActivity : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: android.content.Intent) {
        Log.i(TAG, "onReceive: ")
    }
}
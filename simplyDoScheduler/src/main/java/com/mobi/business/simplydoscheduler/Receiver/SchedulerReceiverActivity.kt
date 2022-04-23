package com.mobi.business.simplydoscheduler.Receiver

import android.content.BroadcastReceiver
import android.content.Context

class SchedulerReceiverActivity : BroadcastReceiver() {

    override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
        val data = intent.getSerializableExtra("data")
        showNotification(context, data)
    }

    private fun showNotification(context: Context, data: Serializable?) {

    }
}
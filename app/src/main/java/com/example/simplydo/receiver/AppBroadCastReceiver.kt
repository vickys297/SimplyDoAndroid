package com.example.simplydo.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.sendNotification

internal val TAG = AppBroadCastReceiver::class.java.canonicalName
class AppBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Log.i(TAG, "onReceive: test")

        if (intent.action == AppConstant.ACTION_ALARM_RECEIVER) {
            Log.i(TAG, "onReceive: action ${intent.action}")

            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            intent.extras?.let {
                notificationManager.sendNotification(
                    it.getLong("dtId"),
                    it.getString("title", ""),
                    it.getString("task", ""),
                    it.getBoolean("priority", true),
                    context
                )
            }

        }


    }


}
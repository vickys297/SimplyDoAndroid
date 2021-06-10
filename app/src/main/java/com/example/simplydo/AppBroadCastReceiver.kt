package com.example.simplydo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

internal val TAG = AppBroadCastReceiver::class.java.canonicalName
class AppBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i(TAG, "onReceive: ")
        TODO("AppBroadCastReceiver.onReceive() is not implemented")
    }
}
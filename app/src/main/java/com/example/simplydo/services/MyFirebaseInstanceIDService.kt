package com.example.simplydo.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    private val TAG = MyFirebaseInstanceIDService::class.java.canonicalName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "onNewToken: $token")
    }
}
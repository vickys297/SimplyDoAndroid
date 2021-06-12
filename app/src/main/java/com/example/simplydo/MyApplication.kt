package com.example.simplydo

import android.app.Application
import android.os.Build
import android.util.Log

internal val TAG_MY = MyApplication::class.java.canonicalName

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG_MY, "onCreate: ${Build.VERSION.RELEASE}/${Build.VERSION.BASE_OS}")
    }


}
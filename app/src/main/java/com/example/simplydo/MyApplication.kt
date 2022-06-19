package com.example.simplydo

import android.app.Application
import android.os.Build
import android.util.Log
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieConfig
import com.example.simplydo.utils.AppWorkManager


internal val TAG_MY = MyApplication::class.java.canonicalName


class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Log.i(TAG_MY, "onCreate: ${Build.VERSION.RELEASE}/${Build.VERSION.BASE_OS}")

        Lottie.initialize(
            LottieConfig.Builder()
                .setEnableSystraceMarkers(true)
                .build()
        )

        val appManger = AppWorkManager.getInstance(this)
        appManger.startWorker()
    }



}
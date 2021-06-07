package com.example.simplydo

import android.app.Application
import com.google.android.libraries.places.api.Places

open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.MAP_API_KEY))

    }

}
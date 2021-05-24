package com.example.simplydo.model

import android.os.Build

data class Token(
    val deviceToken: String,
    val deviceType: Int = 1, // 1 android, 2 ios, 3 web
    val osVersion: String = System.getProperty("os.version")!!,
    val sdkVersion: String = Build.VERSION.SDK_INT.toString(),
    val deviceInfo: String = Build.DEVICE,
    val modelInfo: String = Build.MODEL,
    val productInfo: String = Build.PRODUCT
)


data class TokenResponse(
    val result: String,
    val message: String,
    val uuid: String
)

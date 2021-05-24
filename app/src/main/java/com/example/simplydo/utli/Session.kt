package com.example.simplydo.utli

import android.content.Context

object Session {
    fun saveSession(key: String, value: String, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putString(key, value)
        sharedPreferences.apply()
    }

    fun saveSession(key: String, value: Int, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putInt(key, value)
        sharedPreferences.apply()
    }

    fun saveSession(key: String, value: Boolean, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putBoolean(key, value)
        sharedPreferences.apply()
    }

    fun getSession(key: String, default: String, context: Context): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, default)
    }

    fun getSession(key: String, default: Int, context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, default)
    }

    fun getSession(key: String, default: Boolean, context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(Constant.SESSION_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, default)
    }
}
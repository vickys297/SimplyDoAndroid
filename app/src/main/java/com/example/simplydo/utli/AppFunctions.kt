package com.example.simplydo.utli

import java.util.concurrent.TimeUnit

object AppFunctions {

    fun millisecondsToMinutes(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return "$minutes : ${String.format("%02d", seconds)}"
    }
}
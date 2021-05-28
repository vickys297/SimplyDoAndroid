package com.example.simplydo.utli

import java.text.SimpleDateFormat
import java.util.*

object Constant {


    const val DATABASE_NAME: String = "todoDatabase"

    const val API_RESULT_OK = "OK"
    const val API_RESULT_ERROR = "ERROR"

    const val BASE_URL = "http://192.168.0.100:3000/api/v1beta/"
    const val SESSION_KEY = "appKey"
    const val IS_LOGGED_IN = "isLoggedIn"

    const val VERIFY_WEBSITE = "http://192.168.0.100"

    const val UUID = "UUID"
    const val USER_KEY: String = "uKey"
    const val USER_MOBILE: String = "mobile"


    // date and time stubs
    fun dateFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    fun timeFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }
    fun getCurrentEventDate(): String? {
        val dateFormat = dateFormatter(DATE_PATTERN_COMMON)
        return dateFormat.format(Date().time)
    }

    fun parseStringDateToCalender(date:String): Calendar {
        val simpleDateFormat = dateFormatter(DATE_PATTERN_COMMON)
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(date)!!
        return calendar
    }


    const val TIME_PATTERN = "hh:mm a"
    const val DATE_PATTERN_COMMON = "dd-MM-yyyy"
    private const val DATE_PATTERN_DISPLAY = "dd MMMM yyyy"
    const val DATE_PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val DATE_PATTERN_MONTH_TEXT = "MMM"
    const val DATE_PATTERN_DAY_OF_MONTH = "dd"
}
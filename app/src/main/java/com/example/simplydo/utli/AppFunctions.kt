package com.example.simplydo.utli

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AppFunctions {

    fun millisecondsToMinutes(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return "$minutes : ${String.format("%02d", seconds)}"
    }

    // date and time stubs
    fun dateFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    fun getDateStringFromMilliseconds(milliseconds: Long, datePattern: String): String {
        val formatter = SimpleDateFormat(datePattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }


    fun timeFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    fun getCurrentEventDate(): String? {
        val dateFormat = dateFormatter(AppConstant.DATE_PATTERN_COMMON)
        return dateFormat.format(Date().time)
    }

    fun parseStringDateToCalender(date: String): Calendar {
        val simpleDateFormat = dateFormatter(AppConstant.DATE_PATTERN_COMMON)
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(date)!!
        return calendar
    }

    fun showMessage(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun getDrawableToBitmap(vectorResId: Int, context: Context): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun getHoursOfDay(currentTimeMillis: Long): Int {
        val calender = Calendar.getInstance()
        calender.timeInMillis = currentTimeMillis
        return calender.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinutes(currentTimeMillis: Long): Int {
        val calender = Calendar.getInstance()
        calender.timeInMillis = currentTimeMillis
        return calender.get(Calendar.MINUTE)
    }

    fun getYear(currentTimeMillis: Long): Int {
        val calender = Calendar.getInstance()
        calender.timeInMillis = currentTimeMillis
        return calender.get(Calendar.YEAR)
    }

    fun getMonth(currentTimeMillis: Long): Int {
        val calender = Calendar.getInstance()
        calender.timeInMillis = currentTimeMillis
        return calender.get(Calendar.MONTH)
    }

    fun getDay(currentTimeMillis: Long): Int {
        val calender = Calendar.getInstance()
        calender.timeInMillis = currentTimeMillis
        return calender.get(Calendar.DAY_OF_MONTH)
    }

    fun convertTimeStringToDisplayFormat(eventTime: String): String {
        val et = eventTime.split(":".toRegex())
        val hour = if (et[0].toInt() > 12) et[0].toInt() - 12 else et[0].toInt()
        val minute = et[1].toInt()
        return "$hour : $minute ${if (hour > 11) "AM" else "PM"}"
    }

    fun getCurrentDayMaxInMilliSeconds(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        return calendar.timeInMillis
    }

    fun getCurrentDayMinInMilliSeconds(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }
}
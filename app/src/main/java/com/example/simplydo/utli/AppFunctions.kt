package com.example.simplydo.utli

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import com.example.simplydo.model.TodoModel
import com.example.simplydo.receiver.AppBroadCastReceiver
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
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
        val et = eventTime.trim().split(":".toRegex())
        Log.i(TAG, "convertTimeStringToDisplayFormat: $et")
        val hour = if (et[0].toInt() > 12) et[0].toInt() - 12 else et[0].toInt()
        val minute = et[1].toInt()
        return "$hour : $minute ${if (hour > 11) "PM" else "AM"}"
    }

    fun getCurrentDayEndInMilliSeconds(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        return calendar.timeInMillis
    }

    fun getCurrentDayStartInMilliSeconds(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }

    fun formatSize(v: Long): String {
        if (v < 1024) return "$v B"
        val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
        return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
    }

    fun showSnackBar(view: View, message: String) {
        val snackBar: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }

    @WorkerThread
    fun setNotificationTrigger(
        activity: Activity,
        dtId: String,
        eventTime: Long,
        bundle: Bundle,
        alertType: Int
    ) {
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(activity, AppBroadCastReceiver::class.java)
        intent.action = AppConstant.ACTION_ALARM_RECEIVER
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            dtId.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, eventTime, pendingIntent
        )
    }

    @WorkerThread
    fun checkHasNotificationEnabled(activity: Activity, dtId: String): Boolean {
        val intent = Intent(activity, AppBroadCastReceiver::class.java)
        intent.action = AppConstant.ACTION_ALARM_RECEIVER
        return (PendingIntent.getBroadcast(
            activity,
            dtId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)
    }

    fun stopNotificationTrigger(activity: Activity, dtId: String) {
        val intent = Intent(activity, AppBroadCastReceiver::class.java) //the same as up

        intent.action = AppConstant.ACTION_ALARM_RECEIVER //the same as up

        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            dtId.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        ) //the same as up
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun getCurrentDateCalender(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar
    }

    @WorkerThread
    fun getEventDateText(eventDate: Long): Any {
        val calender = Calendar.getInstance()
        calender.timeInMillis = System.currentTimeMillis()

        // yesterday date
        calender.timeInMillis = getCurrentDayStartInMilliSeconds()
        calender.add(Calendar.DAY_OF_MONTH, -1)
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        val yesterdayStartEventDate = calender.timeInMillis

        calender.set(Calendar.HOUR_OF_DAY, 23)
        calender.set(Calendar.MINUTE, 59)
        calender.set(Calendar.SECOND, 59)
        val yesterdayEndEventDate = calender.timeInMillis

        // current Date
        val currentDateStartEventDate = getCurrentDayStartInMilliSeconds()
        val currentDateEndEventDate = getCurrentDayEndInMilliSeconds()

        // tomorrow date
        calender.timeInMillis = System.currentTimeMillis()
        calender.add(Calendar.DAY_OF_MONTH, 1)
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        val tomorrowStartEventDate = calender.timeInMillis

        calender.set(Calendar.HOUR_OF_DAY, 23)
        calender.set(Calendar.MINUTE, 59)
        calender.set(Calendar.SECOND, 59)
        val tomorrowEndEventDate = calender.timeInMillis

        return when (eventDate) {
            in currentDateStartEventDate..currentDateEndEventDate -> {
                AppConstant.EVENT_TODAY
            }
            in yesterdayStartEventDate..yesterdayEndEventDate -> {
                AppConstant.EVENT_YESTERDAY
            }
            in tomorrowStartEventDate..tomorrowEndEventDate -> {
                AppConstant.EVENT_TOMORROW
            }
            else -> {
                AppConstant.EVENT_CUSTOM
            }
        }
    }

    @WorkerThread
    fun checkForDateTimeExpire(item: TodoModel): Boolean {
        if (item.eventTime.isNotEmpty()) {
            val eventTime = item.eventTime.trim().split(":".toRegex())
            val calender = Calendar.getInstance()
            calender.timeInMillis = item.eventDate
            calender.set(Calendar.HOUR_OF_DAY, eventTime[0].toInt())
            calender.set(Calendar.MINUTE, eventTime[1].toInt())
            calender.set(Calendar.SECOND, 0)
            return calender.timeInMillis < System.currentTimeMillis() && !item.isCompleted
        }

        if (item.eventTime.isEmpty()) {
            return item.eventDate < getCurrentDayStartInMilliSeconds() && !item.isCompleted
        }

        return false
    }

}
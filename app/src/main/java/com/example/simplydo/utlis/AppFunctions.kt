package com.example.simplydo.utlis

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import com.example.simplydo.model.LinkedWorkspaceDataModel
import com.example.simplydo.model.TaskStatusDataModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.receiver.AppBroadCastReceiver
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AppFunctions {

    object Priority {
        fun getPriorityById(i: Int): String {
            return when (i) {
                1 -> {
                    "High"
                }
                2 -> {
                    "Medium"
                }
                3 -> {
                    "Low"
                }
                else -> {
                    "Normal"
                }
            }
        }

    }

    fun millisecondsToMinutes(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return "$minutes : ${String.format("%02d", seconds)}"
    }

    // date and time stubs
    fun dateFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    fun convertTimeInMillsecToPattern(milliseconds: Long, datePattern: String): String {
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

    fun convertTimeStringToDisplayFormat(
        eventDate: Long,
        eventTime: String,
        default: String = "00:00"
    ): String {

        Log.i(TAG, "convertTimeStringToDisplayFormat: $eventTime")

        val et: String = if (eventTime.isNotEmpty()) {
            eventTime
        } else {
            default
        }

        val time = et.split(":".toRegex())
        val hour =
            if (time[0].trim().toInt() > 12) time[0].trim().toInt() - 12 else time[0].trim().toInt()
        val minute = time[1].trim().toInt()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = eventDate

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time).toString()
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
        snackBar.setBackgroundTintMode(PorterDuff.Mode.DARKEN)
        snackBar.show()
    }

    fun showSnackBar(
        view: View,
        message: String,
        actionButtonName: String,
        type: Int,
        task: TodoModel,
        undoInterface: UndoInterface,
    ) {
        val snackBar: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackBar.setAction(actionButtonName) {
            undoInterface.onUndo(task, type)
        }
        snackBar.setBackgroundTintMode(PorterDuff.Mode.DARKEN)
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

    fun getCalender(): Calendar {
        return Calendar.getInstance()
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


    /*@WorkerThread
    fun setupNotification(tasKId: Long, eventDate: Long, bundle: Bundle, activity: Activity) {

        // alert when this is the future task
        if (System.currentTimeMillis() < eventDate) {
            val calendar = getCurrentDateCalender()
            calendar.timeInMillis = eventDate
            calendar.set(Calendar.HOUR_OF_DAY, 7)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            setNotificationTrigger(
                activity,
                tasKId.toString(),
                calendar.timeInMillis,
                bundle,
                AppConstant.ALERT_TYPE_SILENT
            )

            val isEnabled = checkHasNotificationEnabled(
                activity,
                dtId = tasKId.toString()
            )

            Log.i(
                com.example.simplydo.ui.fragments.todoList.TAG,
                "setupNotification: $isEnabled"
            )
        } else {
            Log.i(TAG, "setupNotification: unable to set notification 2")
        }
    }*/

    @WorkerThread
    fun setupNotification(
        tasKId: Long,
        eventDateTime: Long,
        bundle: Bundle,
        activity: Activity
    ) {


        val calendar = getCurrentDateCalender()
        calendar.timeInMillis = eventDateTime


        // alert when this is the future task
        when {
            System.currentTimeMillis() < calendar.timeInMillis -> {
                calendar.set(Calendar.HOUR_OF_DAY, 7)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                setNotificationTrigger(
                    activity,
                    tasKId.toString(),
                    calendar.timeInMillis,
                    bundle,
                    AppConstant.ALERT_TYPE_SILENT
                )

                val isEnabled = checkHasNotificationEnabled(
                    activity,
                    dtId = tasKId.toString()
                )

                Log.i(
                    com.example.simplydo.ui.fragments.todoList.TAG,
                    "setupNotification: $isEnabled"
                )
            }

            // alert when this is the future task
            System.currentTimeMillis() > calendar.timeInMillis -> {
                setNotificationTrigger(
                    activity,
                    tasKId.toString(),
                    calendar.timeInMillis,
                    bundle,
                    AppConstant.ALERT_TYPE_SILENT
                )
                val isEnabled = checkHasNotificationEnabled(
                    activity,
                    dtId = tasKId.toString()
                )

                Log.i(
                    com.example.simplydo.ui.fragments.todoList.TAG,
                    "setupNotification: $isEnabled"
                )

            }
            else -> {
                Log.i(TAG, "setupNotification: unable to set notification 2")
            }
        }
    }

    fun combineEventDateEventTimeAsCalender(eventDate: Long, eventTime: String): Calendar {

        val evenTime = if (eventTime.isNotEmpty()) eventTime else "00:00"


        Log.i(TAG, "combineEventDateEventTimeAsCalender: $eventTime")
        val et = evenTime.trim().split(":".toRegex())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = eventDate
        calendar.set(Calendar.HOUR_OF_DAY, et[0].trim().toInt())
        calendar.set(Calendar.MINUTE, et[1].trim().toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    fun loadDefault() {

    }

    fun getPatternByTimeInMilliSec(selectedDueTime: Long, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(selectedDueTime).time)
    }

    fun changeWorkspace(item: LinkedWorkspaceDataModel, context: Context) {
        AppPreference.storePreferences(
            AppConstant.Preferences.WORKSPACE_DATA,
            Gson().toJson(item),
            context
        )
    }

    fun getTaskStatus(): ArrayList<TaskStatusDataModel> {
        return arrayListOf(
            TaskStatusDataModel(
                statusName = "Open",
                statusId = AppConstant.Task.TASK_STATUS_OPEN
            ),
            TaskStatusDataModel(
                statusName = "In Progress",
                statusId = AppConstant.Task.TASK_STATUS_IN_PROGRESS
            ),
            TaskStatusDataModel(
                statusName = "On Hold",
                statusId = AppConstant.Task.TASK_STATUS_ON_HOLD
            ),
            TaskStatusDataModel(
                statusName = "Completed",
                statusId = AppConstant.Task.TASK_STATUS_COMPLETED
            )
        )
    }


    object Permission {

        fun checkPermission(permissionString: String, context: Context): Boolean {
            val checkPermissionRequest =
                ContextCompat.checkSelfPermission(context, permissionString)
            return checkPermissionRequest == PackageManager.PERMISSION_GRANTED
        }

        fun checkPermission(permissionStrings: Array<String>, context: Context): Boolean {
            var flag = true
            for (permissionString in permissionStrings) {
                val checkPermissionRequest =
                    ContextCompat.checkSelfPermission(
                        context,
                        permissionString
                    )
                if (checkPermissionRequest != PackageManager.PERMISSION_DENIED) {
                    flag = false
                }
            }
            return flag
        }

    }

}
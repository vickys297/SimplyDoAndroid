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

object AppConstant {



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

    // view past order in other fragments
    const val TODO_VIEW_PAST: Int = 1

    // view completed order in other fragments
    const val TODO_VIEW_COMPLETED: Int = 2


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

    fun parseStringDateToCalender(date: String): Calendar {
        val simpleDateFormat = dateFormatter(DATE_PATTERN_COMMON)
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


    const val TIME_PATTERN = "hh:mm a"
    const val DATE_PATTERN_COMMON = "dd-MM-yyyy"
    private const val DATE_PATTERN_DISPLAY = "dd MMMM yyyy"
    const val DATE_PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val DATE_PATTERN_MONTH_TEXT = "MMM"
    const val DATE_PATTERN_DAY_OF_MONTH = "dd"


    // Work Manger

    const val KEY_RESULT = "ContactListResult"


    // navigation data call back
    const val NAVIGATION_CONTACT_DATA_KEY: String ="SelectedContactList"
    const val NAVIGATION_AUDIO_DATA_KEY: String ="SelectedAudioList"
    const val NAVIGATION_GALLERY_DATA_KEY: String ="SelectedGalleryList"
}
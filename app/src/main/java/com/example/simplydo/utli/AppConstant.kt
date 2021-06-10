package com.example.simplydo.utli

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

    const val TIME_PATTERN_EVENT_TIME = "hh:mm a"
    const val DATE_PATTERN_COMMON = "dd-MM-yyyy"
    const val DATE_PATTERN_EVENT_DATE = "dd-MM-yyyy"
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
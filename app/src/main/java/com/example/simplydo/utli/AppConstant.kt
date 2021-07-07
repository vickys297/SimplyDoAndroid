package com.example.simplydo.utli

object MimeTypes {
    object Application {
        const val ATOM_XML = "application/atom+xml"
        const val ATOMCAT_XML = "application/atomcat+xml"
        const val ECMASCRIPT = "application/ecmascript"
        const val JAVA_ARCHIVE = "application/java-archive"
        const val JAVASCRIPT = "application/javascript"
        const val JSON = "application/json"
        const val MP4 = "application/mp4"
        const val OCTET_STREAM = "application/octet-stream"
        const val PDF = "application/pdf"
        const val PKCS_10 = "application/pkcs10"
        const val PKCS_7_MIME = "application/pkcs7-mime"
        const val PKCS_7_SIGNATURE = "application/pkcs7-signature"
        const val PKCS_8 = "application/pkcs8"
        const val POSTSCRIPT = "application/postscript"
        const val RDF_XML = "application/rdf+xml"
        const val RSS_XML = "application/rss+xml"
        const val RTF = "application/rtf"
        const val SMIL_XML = "application/smil+xml"
        const val X_FONT_OTF = "application/x-font-otf"
        const val X_FONT_TTF = "application/x-font-ttf"
        const val X_FONT_WOFF = "application/x-font-woff"
        const val X_PKCS_12 = "application/x-pkcs12"
        const val X_SHOCKWAVE_FLASH = "application/x-shockwave-flash"
        const val X_SILVERLIGHT_APP = "application/x-silverlight-app"
        const val XHTML_XML = "application/xhtml+xml"
        const val XML = "application/xml"
        const val XML_DTD = "application/xml-dtd"
        const val XSLT_XML = "application/xslt+xml"
        const val ZIP = "application/zip"
    }

    object Audio {
        const val MIDI = "audio/midi"
        const val MP4 = "audio/mp4"
        const val MPEG = "audio/mpeg"
        const val OGG = "audio/ogg"
        const val WEBM = "audio/webm"
        const val X_AAC = "audio/x-aac"
        const val X_AIFF = "audio/x-aiff"
        const val X_MPEGURL = "audio/x-mpegurl"
        const val X_MS_WMA = "audio/x-ms-wma"
        const val X_WAV = "audio/x-wav"
    }

    object Image {
        const val BMP = "image/bmp"
        const val GIF = "image/gif"
        const val JPEG = "image/jpeg"
        const val PNG = "image/png"
        const val SVG_XML = "image/svg+xml"
        const val TIFF = "image/tiff"
        const val WEBP = "image/webp"
    }

    object Text {
        const val CSS = "text/css"
        const val CSV = "text/csv"
        const val HTML = "text/html"
        const val PLAIN = "text/plain"
        const val RICH_TEXT = "text/richtext"
        const val SGML = "text/sgml"
        const val YAML = "text/yaml"
    }

    object Video {
        const val THREEGPP = "video/3gpp"
        const val H264 = "video/h264"
        const val MP4 = "video/mp4"
        const val MPEG = "video/mpeg"
        const val OGG = "video/ogg"
        const val QUICKTIME = "video/quicktime"
        const val WEBM = "video/webm"
    }
}
object AppConstant {

    // task
    object Task {
        const val TASK_ACTION_RESTORE: Int = 10
        const val TASK_ACTION_DELETE: Int = 11
        const val TASK_ACTION_EDIT: Int = 12
        const val TASK_ACTION_NEW: Int = 13

        const val TASK_TYPE_DEFAULT: Int = 100
        const val TASK_TYPE_BASIC: Int = 100
        const val TASK_TYPE_EVENT: Int = 101
        const val TASK_TYPE_ATTACHMENT: Int = 102
    }

    object StartUp {
        const val ONBOARD_COMPLETED: String = "onBoardCompleted"
    }


    const val NOTIFICATION_TASK_NEW: Int = 1000
    const val NOTIFICATION_TASK_DAILY_REMAINDER: Int = 1001

    // Silent alert user about the upcoming task
    const val ALERT_TYPE_SILENT: Int = 15

    // Alert user show notification at the task event date time
    const val ALERT_TYPE_NOTIFY: Int = 11

    const val ACTION_COMPLETE: String = "action_task_complete"
    const val ACTION_VIEW: String = "action_task_view"
    const val ACTION_VIEW_MY_TASK: String = "action_task_view_my_task"

    const val DATABASE_NAME: String = "todoDatabase"

    const val DEFAULT_PAGE_SIZE = 25

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


    const val DATE_PATTERN_EVENT_DATE = "dd-MM-yyyy"
    const val DATE_PATTERN_EVENT_DATE_TIME = "dd-MM-yyyy HH:mm:ss a"
    const val TIME_PATTERN_EVENT_TIME = "hh:mm a"
    const val DATE_PATTERN_COMMON = "dd-MM-yyyy"

    private const val DATE_PATTERN_DISPLAY = "dd MMMM yyyy"
    const val DATE_PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val DATE_PATTERN_MONTH_TEXT = "MMM"
    const val DATE_PATTERN_DAY_OF_MONTH = "dd"

    const val EVENT_TODAY = "Today"
    const val EVENT_TOMORROW = "Tomorrow"
    const val EVENT_YESTERDAY = "Yesterday"
    const val EVENT_CUSTOM = "Custom"


    // Work Manger

    const val KEY_RESULT = "ContactListResult"


    // navigation data call back
    const val NAVIGATION_CONTACT_DATA_KEY: String = "SelectedContactList"
    const val NAVIGATION_FILES_DATA_KEY: String = "SelectedFilesList"
    const val NAVIGATION_AUDIO_DATA_KEY: String = "SelectedAudioList"
    const val NAVIGATION_GALLERY_DATA_KEY: String = "SelectedGalleryList"
    const val NAVIGATION_LOCATION_DATA_KEY: String = "SelectedLocation"

    const val NAVIGATION_TASK_ACTION_EDIT_KEY: String ="navigation_action_edit_key"
    const val NAVIGATION_TASK_DATA_KEY: String ="task_data"
    const val NAVIGATION_TASK_KEY: String = "todo_dtid"


    // broadcast receiver

    const val ACTION_ALARM_RECEIVER = "taskNotification"

    const val ACTION_DISMISS =
        "com.example.android.wearable.wear.wearnotifications.handlers.action.DISMISS"

    const val ACTION_SNOOZE =
        "com.example.android.wearable.wear.wearnotifications.handlers.action.SNOOZE"
}
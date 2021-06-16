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
    const val NAVIGATION_FILES_DATA_KEY: String ="SelectedFilesList"
    const val NAVIGATION_AUDIO_DATA_KEY: String ="SelectedAudioList"
    const val NAVIGATION_GALLERY_DATA_KEY: String ="SelectedGalleryList"
    const val NAVIGATION_LOCATION_DATA_KEY: String ="SelectedLocation"
}
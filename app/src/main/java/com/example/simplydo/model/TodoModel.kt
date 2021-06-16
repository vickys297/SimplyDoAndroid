package com.example.simplydo.model

import android.util.Log
import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


@Entity(tableName = "todoList", indices = [Index(value = ["dtId"], unique = true)])
data class TodoModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dtId")
    val dtId: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "todo")
    val todo: String,

    // event date time
    @ColumnInfo(name = "eventTime")
    val eventTime: String,
    @ColumnInfo(name = "eventDate")
    val eventDate: Long,

    // priority
    @ColumnInfo(name = "isHighPriority", defaultValue = "1")
    val isHighPriority: Boolean = true,

    // attachments
    @ColumnInfo(name = "locationData", defaultValue = "")
    val locationData: String = "",
    @ColumnInfo(name = "contactAddress", defaultValue = "")
    val contactAttachments: ArrayList<ContactModel> = ArrayList(),
    @ColumnInfo(name = "galleryFiles", defaultValue = "")
    val imageAttachments: ArrayList<GalleryModel> = ArrayList(),
    @ColumnInfo(name = "audioFiles", defaultValue = "")
    val audioAttachments: ArrayList<AudioModel> = ArrayList(),
    @ColumnInfo(name = "documentFiles", defaultValue = "")
    val fileAttachments: ArrayList<FileModel> = ArrayList(),

    // entries time stamp
    @ColumnInfo(name = "createdAt", defaultValue = "")
    val createdAt: String,
    @ColumnInfo(name = "updatedAt", defaultValue = "")
    val updatedAt: String,

    // is database synced with cloud database
    @ColumnInfo(name = "synchronize", defaultValue = "0")
    var synchronize: Boolean = false,

    // task  status
    @ColumnInfo(name = "isCompleted", defaultValue = "0")
    var isCompleted: Boolean = false,
    @ColumnInfo(name = "completedDateTime", defaultValue = "")
    var completedDateTime: String = "",
) : Serializable {

    fun isVisible(): Int {
        return if (isHighPriority) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun dateExpiredVisibility(): Int {

        if (eventTime.isEmpty()) {
            return View.GONE
        }


        val currentDate = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.set(
            AppFunctions.getYear(eventDate),
            AppFunctions.getMonth(eventDate),
            AppFunctions.getDay(eventDate),
            eventTime.split(":")[0].toInt(),
            eventTime.split(":")[1].toInt()
        )

        Log.i("Model", "dateExpiredVisibility: $title --> ${calendar.timeInMillis}/$currentDate")

        return if (calendar.timeInMillis < currentDate) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun getEventDateTextValue(): String {
        val currentEventDate =
            AppFunctions.getDateStringFromMilliseconds(
                eventDate,
                AppConstant.DATE_PATTERN_EVENT_DATE
            )
        val currentDate = AppFunctions.getDateStringFromMilliseconds(
            System.currentTimeMillis(),
            AppConstant.DATE_PATTERN_EVENT_DATE
        )

        return if (currentEventDate == currentDate) {
            "Today"
        } else {
            AppFunctions.getDateStringFromMilliseconds(
                eventDate,
                AppConstant.DATE_PATTERN_EVENT_DATE
            )
        }
    }
}

data class TodoPagingModel(
    val data: ArrayList<TodoModel>,
    var nextPage: Int
)


data class ContactModel(
    val id: Long? = null,
    val photoThumbnailUri: String?,
    val photoUri: String?,
    val name: String,
    val mobile: String,
    var isSelected: Boolean = false
)


data class RequestDataFromCloudResponseModel(
    val result: String,
    val message: String,
    val data: ArrayList<TodoModel>,
)


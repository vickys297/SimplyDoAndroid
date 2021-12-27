package com.example.simplydo.model

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import java.io.Serializable


@Entity(tableName = "todoList", indices = [Index(value = ["title"], unique = true)])
data class TodoModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dtId")
    val dtId: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "todo")
    val todo: String,

    @ColumnInfo(name = "eventDate")
    val eventDateTime: Long,

    // priority
    @ColumnInfo(name = "taskPriority", defaultValue = "3")
    val taskPriority: Int = 3,

    // attachments
    @ColumnInfo(name = "locationData", defaultValue = "")
    val locationData: LatLngModel = LatLngModel(),

    @ColumnInfo(name = "contactAddress", defaultValue = "")
    val contactAttachments: ArrayList<ContactModel> = ArrayList(),

    @ColumnInfo(name = "galleryFiles", defaultValue = "")
    val galleryAttachments: ArrayList<GalleryModel> = ArrayList(),

    @ColumnInfo(name = "audioFiles", defaultValue = "")
    val audioAttachments: ArrayList<AudioModel> = ArrayList(),

    @ColumnInfo(name = "documentFiles", defaultValue = "")
    val fileAttachments: ArrayList<FileModel> = ArrayList(),

    // entries time stamp
    @ColumnInfo(name = "createdAt", defaultValue = "")
    val createdAt: String = "",

    @ColumnInfo(name = "updatedAt", defaultValue = "")
    val updatedAt: String = "",

    // is database synced with cloud database
    @ColumnInfo(name = "synchronize", defaultValue = "0")
    var synchronize: Boolean = false,

    // task  status
    @ColumnInfo(name = "isCompleted", defaultValue = "0")
    var isCompleted: Boolean = false,

    @ColumnInfo(name = "completedDateTime", defaultValue = "")
    var completedDateTime: String = "",

    @ColumnInfo(name = "deleted", defaultValue = "0")
    var deleted: Boolean = false,

    @ColumnInfo(name = "deletedDateTimeStamp", defaultValue = "")
    var deletedDateTimeStamp: String = "",

    @ColumnInfo(name = "taskType", defaultValue = AppConstant.Task.TASK_TYPE_DEFAULT.toString())
    var taskType: Int = AppConstant.Task.TASK_TYPE_DEFAULT,

    @ColumnInfo(name = "repeatFrequency")
    var repeatFrequency: ArrayList<SelectorDataModal> = arrayListOf(),

    @ColumnInfo(name = "repeatWeek")
    var repeatDays: ArrayList<SelectorDataModal> = arrayListOf(),

    @ColumnInfo(name = "links")
    var links: ArrayList<LinksModel> = arrayListOf(),

    @ColumnInfo(name = "tags")
    var taskTags: ArrayList<TagModel> = arrayListOf(),

    @ColumnInfo(name = "todoTaskList", defaultValue = "")
    val arrayListTodoTask: ArrayList<TodoTaskModel> = arrayListOf()

) : Serializable {
    fun isCompletedVisible(): Int {
        return if (isCompleted) View.VISIBLE else View.GONE
    }

    fun isPriorityVisible(): Int {
        return View.VISIBLE
    }

    fun getEventDate(): String {
        return when (AppFunctions.getEventDateText(eventDateTime)) {
            AppConstant.EVENT_TODAY -> {
                AppConstant.EVENT_TODAY
            }
            AppConstant.EVENT_TOMORROW -> {
                AppConstant.EVENT_TOMORROW
            }
            AppConstant.EVENT_YESTERDAY -> {
                AppConstant.EVENT_YESTERDAY
            }
            else -> {
                AppFunctions.convertTimeInMillsecToPattern(
                    eventDateTime,
                    AppConstant.DATE_PATTERN_EVENT_DATE
                )
            }
        }
    }

    fun isEventTimeVisible(): Int {
        return if (taskType == AppConstant.Task.TASK_TYPE_BASIC) View.GONE else View.VISIBLE
    }

    fun getEventTime(): String {
        return if (taskType == AppConstant.Task.TASK_TYPE_EVENT) {
            AppFunctions.convertTimeInMillsecToPattern(
                eventDateTime,
                AppConstant.TIME_PATTERN_EVENT_TIME
            )
        } else {
            ""
        }
    }


    fun isDateExpiredVisible(): Int {
        return if (System.currentTimeMillis() < eventDateTime) View.GONE else View.VISIBLE
    }

    fun isAttachmentVisible(): Int {
        return if (audioAttachments.isEmpty() &&
            galleryAttachments.isEmpty() &&
            audioAttachments.isEmpty() &&
            contactAttachments.isEmpty() &&
            locationData.lat == 0.0 &&
            locationData.lng == 0.0
        ) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun getTaskPriorityText(): String {
        return when (taskPriority) {
            AppConstant.TaskPriority.HIGH_PRIORITY -> {
                AppConstant.TaskPriority.TextValue.HIGH_PRIORITY
            }
            AppConstant.TaskPriority.MEDIUM_PRIORITY -> {
                AppConstant.TaskPriority.TextValue.MEDIUM_PRIORITY
            }
            AppConstant.TaskPriority.LOW_PRIORITY -> {
                AppConstant.TaskPriority.TextValue.LOW_PRIORITY
            }
            else -> {
                AppConstant.TaskPriority.TextValue.LOW_PRIORITY
            }
        }
    }


    fun isEmptyStateVisible(): Int {
        return if (audioAttachments.isEmpty() &&
            galleryAttachments.isEmpty() &&
            audioAttachments.isEmpty() &&
            contactAttachments.isEmpty() &&
            locationData.lat == 0.0 &&
            locationData.lng == 0.0
        ) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

data class LatLngModel(
    var lat: Double = 0.0,
    var lng: Double = 0.0
) : Serializable

data class TodoPagingModel(
    val data: ArrayList<TodoModel>,
    var nextPage: Int
)

data class TodoTaskModel(
    val type: Int,
    val content: String? = "",
    var isCompleted :Boolean = false,
    val contentList: ArrayList<String>? = ArrayList()
) : Serializable


data class ContactModel(
    val id: Long? = null,
    val photoThumbnailUri: String?,
    val photoUri: String?,
    val name: String,
    var mobile: ArrayList<String> = ArrayList(),
    var email: ArrayList<String> = ArrayList(),
    var isSelected: Boolean = false
)


data class RequestDataFromCloudResponseModel(
    val result: String,
    val message: String,
    val data: ArrayList<TodoModel>,
)


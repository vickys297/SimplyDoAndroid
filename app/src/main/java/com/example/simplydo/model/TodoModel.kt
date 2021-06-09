package com.example.simplydo.model

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simplydo.utli.AppConstant

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
    val eventDate: String,

    // priority
    @ColumnInfo(name = "isHighPriority", defaultValue = "1")
    val isHighPriority: Boolean = true,

    // attachments
    @ColumnInfo(name = "locationInfo", defaultValue = "")
    val locationInfo: String = "",
    @ColumnInfo(name = "contactInfo", defaultValue = "")
    val contactInfo: ArrayList<ContactModel>,
    @ColumnInfo(name = "imageFiles", defaultValue = "")
    val imageFiles: ArrayList<String>,

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
) {

    fun isVisible(): Int {
        return if (isHighPriority) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun getEventTextValue(): String {
        return if (eventTime.isEmpty()) {
            if (eventDate == AppConstant.getCurrentEventDate()) {
                "Today"
            } else {
                eventDate
            }
        } else {
            eventTime
        }
    }
}

data class TodoPagingModel(
    val data: ArrayList<TodoModel>,
    var nextPage: Int
)


data class ContactModel(
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


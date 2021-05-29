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
    @ColumnInfo(name = "isHighPriority", defaultValue = "true")
    val isHighPriority: Boolean = true,

    // attachments
    @ColumnInfo(name = "locationInfo", defaultValue = "")
    val locationInfo: String = "",
    @ColumnInfo(name = "contactInfo", defaultValue = "")
    val contactInfo: ArrayList<ContactInfo>,
    @ColumnInfo(name = "imageFiles", defaultValue = "")
    val imageFiles: ArrayList<String>,

    // entries time stamp
    @ColumnInfo(name = "createdAt", defaultValue = "")
    val createdAt: String,
    @ColumnInfo(name = "updatedAt", defaultValue = "")
    val updatedAt: String,

    // is database synced with cloud database
    @ColumnInfo(name = "synchronize", defaultValue = "0")
    var synchronize: Int = 0,

    // task  status
    @ColumnInfo(name = "isCompleted", defaultValue = "0")
    var isCompleted: Int = 0,
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

data class ContactInfo(
    val name: String,
    val mobile: String,
)


data class RequestDataFromCloudResponseModel(
    val result: String,
    val message: String,
    val data: ArrayList<TodoModel>,
)


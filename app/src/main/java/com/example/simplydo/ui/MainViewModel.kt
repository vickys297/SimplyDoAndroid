package com.example.simplydo.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository
import java.util.*

class MainViewModel(val context: Context, val appRepository: AppRepository) : ViewModel() {

    fun getAllTodoListNotSynced(): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getAllTodoNotSynced()
    }

    fun syncDataWithCloud(arrayList: ArrayList<TodoModel>) {
        appRepository.uploadDataToCloudDatabase(arrayList)
    }

    fun getTotalTaskCount(): LiveData<Int> {
        return appRepository.appDatabase.todoDao().getTotalTaskCount()
    }

    fun insertDummyDataIntoLocalDatabase(
        task: String,
        title: String,
        eventDate: Long,
        priority: Boolean,
        contactList: ArrayList<ContactModel>,
        imageList: ArrayList<GalleryModel>,
    ) {
        appRepository.insertNewTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventTime = "",
                eventDate = eventDate,
                contactAttachments = contactList,
                imageAttachments = imageList,
                locationData = "",
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isHighPriority = priority
            ))
    }
}
package com.example.simplydo.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppConstant
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
        eventDate: String,
        priority: Boolean,
        contactList: ArrayList<ContactModel>,
        imageList: ArrayList<String>,
    ) {
        appRepository.insertNewTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventTime = "",
                eventDate = eventDate,
                contactInfo = contactList,
                imageFiles = imageList,
                locationInfo = "",
                createdAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isHighPriority = priority
            ))
    }
}
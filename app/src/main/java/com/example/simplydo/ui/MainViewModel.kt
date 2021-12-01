package com.example.simplydo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.TagModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.AppRepository
import java.util.*

class MainViewModel(val appRepository: AppRepository) : ViewModel() {

    fun getAllTodoListNotSynced(): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getAllTodoNotSynced()
    }

    fun syncDataWithCloud(arrayList: ArrayList<TodoModel>) {
        appRepository.uploadDataToCloudDatabase(arrayList)
    }


    fun insertDummyDataIntoLocalDatabase(
        task: String,
        title: String,
        eventDate: Long,
        priority: Boolean,
        isCompleted: Boolean = false,
        contactList: ArrayList<ContactModel>,
        imageList: ArrayList<GalleryModel>,
        taskType: Int
    ) {
        appRepository.reinsertTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                contactAttachments = contactList,
                imageAttachments = imageList,
                locationData = LatLngModel(),
                isCompleted = isCompleted,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isHighPriority = priority,
                taskType = taskType
            )
        )
    }


    private fun loadTags() {

    }

    fun getAvailableTagList(): ArrayList<TagModel> {
        return appRepository.getAvailableTags()
    }

    fun insertTag(tag: String) {
        appRepository.insertTag(tag)
    }
}
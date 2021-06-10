package com.example.simplydo.ui.fragments.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository
import java.util.*

class CalenderViewModel(val appRepository: AppRepository) : ViewModel() {

    val nextAvailableDate = MutableLiveData<List<TodoModel>>()

    fun getTodoListByEventDate(date: String): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getTodoByEventDate(date)
    }

    fun requestDataFromCloud(selectedEventDate: String) {
        appRepository.downloadTaskByDate(selectedEventDate)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: Long,
        eventTime: String,
        priority: Boolean,
        contactInfo: ArrayList<ContactModel>,
        imagesList: ArrayList<GalleryModel>,
    ) {
        appRepository.insertNewTodoTask(TodoModel(
            title = title,
            todo = task,
            eventTime = eventTime,
            eventDate = eventDate,
            contactAttachments = contactInfo,
            imageAttachments = imagesList,
            locationData = "",
            createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
            updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
            isHighPriority = priority
        ))

    }

    fun getNextTaskAvailability(selectedEventDate: String) {
        appRepository.getNextTaskAvailability(selectedEventDate, nextAvailableDate)
    }

    fun removeTaskById(task: TodoModel) {
        appRepository.deleteTaskByPosition(task)
    }

    fun completeTaskByID(dtId: Long) {
        appRepository.completeTaskById(dtId)
    }

}
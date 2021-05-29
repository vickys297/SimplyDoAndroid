package com.example.simplydo.ui.fragments.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppRepository
import java.util.*

class CalenderViewModel(val appRepository: AppRepository) : ViewModel() {

    val nextAvailableDate = MutableLiveData<List<TodoModel>>()

    fun getTodoListByEventDate(date: String): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getTodoByEnetDate(date)
    }

    fun requestDataFromCloud(selectedEventDate: String) {
        appRepository.downloadTaskByDate(selectedEventDate)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: String,
        priority: Boolean,
        contactInfo: ArrayList<ContactInfo>,
        imagesList: ArrayList<String>,
    ) {
        appRepository.insertNewTodoTask(TodoModel(
            title = title,
            todo = task,
            eventTime = "",
            eventDate = eventDate,
            contactInfo = contactInfo,
            imageFiles = imagesList,
            locationInfo = "",
            createdAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
            updatedAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
            isHighPriority = priority
        ))

//        repository.uploadNewTodo(
//            TodoModel(
//                dtId = lastId,
//                title = title,
//                todo = task,
//                eventTime = "",
//                eventDate = eventDate,
//                contactInfo = contactInfo,
//                imageFiles = imagesList,
//                locationInfo = "",
//                createdAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
//                updatedAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
//                isHighPriority = priority),
//            Session.getSession(Constant.USER_KEY, context),
//            todoListResponse,
//            noNetworkMessage)
    }

    fun getNextTaskAvailability(selectedEventDate: String) {
        appRepository.getNextTaskAvailability(selectedEventDate, nextAvailableDate)
    }

}
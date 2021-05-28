package com.example.simplydo.screens.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.Constant
import com.example.simplydo.utli.Repository
import java.util.*

class CalenderViewModel(val repository: Repository) : ViewModel() {

    val nextAvailableDate = MutableLiveData<List<TodoModel>>()

    fun getTodoListByEventDate(date: String): LiveData<List<TodoModel>> {
        return repository.appDatabase.todoDao().getTodoByEnetDate(date)
    }

    fun requestDataFromCloud(selectedEventDate: String) {
        repository.downloadTaskByDate(selectedEventDate)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: String,
        priority: Boolean,
        contactInfo: ArrayList<ContactInfo>,
        imagesList: ArrayList<String>,
    ) {
        repository.insertNewTodoTask(TodoModel(
            title = title,
            todo = task,
            eventTime = "",
            eventDate = eventDate,
            contactInfo = contactInfo,
            imageFiles = imagesList,
            locationInfo = "",
            createdAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
            updatedAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
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
        repository.getNextTaskAvailability(selectedEventDate, nextAvailableDate)
    }

}
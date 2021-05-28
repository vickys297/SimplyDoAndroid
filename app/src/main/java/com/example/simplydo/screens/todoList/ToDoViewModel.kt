package com.example.simplydo.screens.todoList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.Constant
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.Session
import java.util.*


class ToDoViewModel(private val context: Context, private val repository: Repository) :
    ViewModel() {

    // TODO: Implement the ViewModel
    val todoListResponse = MutableLiveData<CommonResponseModel>()
    val noNetworkMessage = MutableLiveData<String>()


    fun todoListObserver(eventDate: String): LiveData<List<TodoModel>> {
        return repository.appDatabase.todoDao().getAllTodo(eventDate)
    }


    fun removeTaskById(id: Long) {
        return repository.deleteTaskByPosition(id)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: String,
        priority: Boolean,
        contactInfo: ArrayList<ContactInfo>,
        imagesList: ArrayList<String>,
    ) {
        val lastId = repository.insertNewTodoTask(
            TodoModel(
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

        repository.uploadNewTodo(
            TodoModel(
                dtId = lastId,
                title = title,
                todo = task,
                eventTime = "",
                eventDate = eventDate,
                contactInfo = contactInfo,
                imageFiles = imagesList,
                locationInfo = "",
                createdAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
                updatedAt = Constant.dateFormatter(Constant.DATE_PATTERN_ISO).format(Date().time),
                isHighPriority = priority),
            Session.getSession(Constant.USER_KEY, context),
            todoListResponse,
            noNetworkMessage)
    }

    fun syncDataWithDatabase(dateString: String) {
        repository.uploadDataToCloudDatabase()
        repository.downloadTaskByDate(dateString)
    }

    fun completeTaskByID(dtId: Long) {
        repository.completeTaskById(dtId)
    }

}
package com.example.simplydo.ui.fragments.todoList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppPreference
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.AppConstant
import java.util.*


class ToDoViewModel(private val context: Context, private val appRepository: AppRepository) :
    ViewModel() {

    // TODO: Implement the ViewModel
    val todoListResponse = MutableLiveData<CommonResponseModel>()
    val noNetworkMessage = MutableLiveData<String>()


    fun todoListObserver(eventDate: String): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getAllTodo(eventDate)
    }


    fun removeTaskById(id: Long) {
        return appRepository.deleteTaskByPosition(id)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: String,
        priority: Boolean,
        contactInfo: ArrayList<ContactInfo>,
        imagesList: ArrayList<String>,
    ) {
        val lastId = appRepository.insertNewTodoTask(
            TodoModel(
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

        appRepository.uploadNewTodo(
            TodoModel(
                dtId = lastId,
                title = title,
                todo = task,
                eventTime = "",
                eventDate = eventDate,
                contactInfo = contactInfo,
                imageFiles = imagesList,
                locationInfo = "",
                createdAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
                updatedAt = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_ISO).format(Date().time),
                isHighPriority = priority),
            AppPreference.getPreferences(AppConstant.USER_KEY, context),
            todoListResponse,
            noNetworkMessage)
    }

    fun syncDataWithDatabase(dateString: String) {
        appRepository.uploadDataToCloudDatabase()
        appRepository.downloadTaskByDate(dateString)
    }

    fun completeTaskByID(dtId: Long) {
        appRepository.completeTaskById(dtId)
    }

}
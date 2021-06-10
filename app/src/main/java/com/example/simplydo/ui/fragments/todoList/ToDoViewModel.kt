package com.example.simplydo.ui.fragments.todoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository
import java.util.*


class ToDoViewModel(private val appRepository: AppRepository) :
    ViewModel() {

    // TODO: Implement the ViewModel
    val todoListResponse = MutableLiveData<CommonResponseModel>()
    val noNetworkMessage = MutableLiveData<String>()


    fun todoListObserver(eventDate: String): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getTodoForQuickView(eventDate)
    }


    fun removeTaskById(item :TodoModel) {
        return appRepository.deleteTaskByPosition(item)
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
        appRepository.insertNewTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventTime = eventTime,
                eventDate = eventDate,
                contactAttachments = contactInfo,
                imageAttachments = imagesList,
                locationData = "",
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isHighPriority = priority
            ))

    }


    fun completeTaskByID(dtId: Long) {
        appRepository.completeTaskById(dtId)
    }

}
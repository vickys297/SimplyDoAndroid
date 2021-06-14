package com.example.simplydo.ui.fragments.quickTodoList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository
import kotlinx.coroutines.flow.Flow
import java.util.*


internal const val PAGE_SIZE = 30

class QuickTodoViewModel(private val appRepository: AppRepository) :
    ViewModel() {

    // TODO: Implement the ViewModel
    val todoListResponse = MutableLiveData<CommonResponseModel>()
    val noNetworkMessage = MutableLiveData<String>()


    fun removeTaskById(item: TodoModel) {
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
        val inserted = appRepository.insertNewTodoTask(
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
            )
        )
        Log.i(TAG, "createNewTodo: $inserted")
    }


    fun completeTaskByID(dtId: Long) {
        appRepository.completeTaskById(dtId)
    }

    fun getQuickTodoList(
        eventStartDateTime: Long
    ): Flow<PagingData<TodoModel>> {
        return Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            appRepository.appDatabase.todoDao().getQuickTodoList(eventDate = eventStartDateTime)
        }.flow
            .cachedIn(viewModelScope)
    }

}
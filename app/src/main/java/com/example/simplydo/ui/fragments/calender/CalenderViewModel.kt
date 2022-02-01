package com.example.simplydo.ui.fragments.calender

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.ui.fragments.todoList.PAGE_SIZE
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class CalenderViewModel(val appRepository: AppRepository) : ViewModel() {

    val nextAvailableDate = MutableLiveData<TodoModel>()
    val selectedEventDateTotalCount = MutableLiveData<Int>()

    fun getTodoListByEventDate(
        startEventDate: Long,
        endEventDate: Long
    ): Flow<PagingData<TodoModel>> {
        return Pager(
            PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)
        ) {
            appRepository.appDatabase.todoDao().getSingleDayTodoList(startEventDate, endEventDate)
        }.flow.cachedIn(viewModelScope)
    }


    fun createNewTodo(
        title: String,
        task: String,
        eventDate: Long,
        priority: Boolean,
        contactInfo: ArrayList<ContactModel>,
        imagesList: ArrayList<GalleryModel>,
    ) {
        appRepository.insertTodoTask(TodoModel(
            title = title,
            todo = task,
            eventDateTime = eventDate,
            taskPriority = 3,
            locationData = LatLngModel(),
            contactAttachments = contactInfo,
            galleryAttachments = imagesList,
            createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                .format(Date().time),
            updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                .format(Date().time)
        )
        )

    }

    fun getNextTaskAvailability(selectedEventDate: Long) {
        appRepository.getNextTaskAvailability(selectedEventDate, nextAvailableDate)
    }

    fun getSelectedEventDateItemCount(startEventDate: Long, endEventDate: Long) {
        appRepository.getSelectedEventDateItemCount(
            startEventDate,
            endEventDate,
            selectedEventDateTotalCount
        )
    }

    fun removeTask(task: TodoModel) {
        appRepository.deleteTaskByPosition(task)
    }

    fun completeTaskByID(dtId: Long) {
        appRepository.completeTaskById(dtId)
    }


    fun undoTaskRemove(task: TodoModel) {
        appRepository.insertTodoTask(task)
    }
    fun restoreTask(dtId: Long) {
        appRepository.restoreTask(dtId)
    }

}
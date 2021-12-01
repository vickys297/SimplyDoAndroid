package com.example.simplydo.ui.fragments.todoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.flow.Flow
import java.util.*


internal const val PAGE_SIZE = 30

class QuickTodoViewModel(private val appRepository: AppRepository) :
    ViewModel() {

    // TODO: Implement the ViewModel
    val todoListResponse = MutableLiveData<CommonResponseModel>()
    val noNetworkMessage = MutableLiveData<String>()


    fun removeTask(item: TodoModel) {
        return appRepository.deleteTaskByPosition(item)
    }

    fun getTotalTaskCount(eventDateStartTime: Long): LiveData<Int> {
        return appRepository.appDatabase.todoDao().getTotalTaskCount(eventDateStartTime)
    }

    fun createNewTodo(
        title: String,
        task: String,
        eventDate: Long,
        priority: Boolean
    ): Long {
        return appRepository.reinsertTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                isHighPriority = priority,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
            )
        )
    }


    fun completeTaskByID(dtId: Long): Int {
      return appRepository.completeTaskById(dtId)
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

    fun restoreTask(dtId: Long) {
        appRepository.restoreTask(dtId)
    }

    fun undoTaskRemove(task: TodoModel) {
        appRepository.reinsertTodoTask(task)
    }

    fun updateTaskModel(todoModel: TodoModel) {
        appRepository.updateTodoData(todoModel)
    }

}
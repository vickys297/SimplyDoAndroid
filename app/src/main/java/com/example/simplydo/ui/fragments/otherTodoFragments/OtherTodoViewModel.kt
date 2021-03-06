package com.example.simplydo.ui.fragments.otherTodoFragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.AppRepository


class OtherTodoViewModel internal constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    fun removeTask(item: TodoModel) {
        appRepository.deleteTaskByPosition(item)
    }

    fun getPastTaskCount(eventDate: Long): Int {
        return appRepository.checkPastTask(eventDate)
    }

    fun getCompletedCount(currentDateEndTime: Long): Int {
        return appRepository.checkCompletedTask(currentDateEndTime)
    }

    fun restoreTask(dtId: Long) {
        appRepository.restoreTask(dtId = dtId)
    }

    fun undoTaskRemove(task: TodoModel) {
        appRepository.insertTodoTask(task)
    }

    fun completeTaskByID(dtId: Long): Int {
        return appRepository.completeTaskById(dtId = dtId)
    }

    // TODO: Implement the ViewModel
    val flowGetPastTask = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 10,
            enablePlaceholders = false,
            initialLoadSize = 10
        )
    ) {
        appRepository.PastOrderDatasource(AppFunctions.getCurrentDayStartInMilliSeconds())
    }.flow
        .cachedIn(viewModelScope)

    val flowGetCompletedTask = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 10,
            enablePlaceholders = false,
            initialLoadSize = 10
        )
    ) {
        appRepository.getCompletedTask
    }.flow
        .cachedIn(viewModelScope)

}
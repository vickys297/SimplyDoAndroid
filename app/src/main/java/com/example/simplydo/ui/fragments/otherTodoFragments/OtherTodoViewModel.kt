package com.example.simplydo.ui.fragments.otherTodoFragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository


class OtherTodoViewModel internal constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    fun removeTaskById(item: TodoModel) {
        appRepository.deleteTaskByPosition(item)
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
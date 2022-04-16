package com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.ui.activity.personalWorkspace.personalTask.PAGE_SIZE
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.flow.Flow

class WorkspaceGroupTaskViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableWorkspaceGroupTask = MutableLiveData<ArrayList<WorkspaceGroupTaskModel>?>()

    fun getPrivateWorkspaceTaskByGroupId(groupTaskId: Long): Flow<PagingData<WorkspaceGroupTaskModel>> {
        return Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            appRepository.getWorkspaceTaskByGroupId(groupTaskId)
        }.flow
            .cachedIn(viewModelScope)
    }

    fun getAllWorkspaceGroupTaskInLiveData(groupTaskId: Long): LiveData<List<WorkspaceGroupTaskModel>> {
        return appRepository.workspaceGroupTaskDb.getAllWorkspaceGroupTaskInLiveData(groupTaskId)
    }
}
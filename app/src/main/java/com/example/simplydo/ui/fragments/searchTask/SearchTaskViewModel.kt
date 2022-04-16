package com.example.simplydo.ui.fragments.searchTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.ui.activity.personalWorkspace.personalTask.PAGE_SIZE
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SearchTaskViewModel(val appRepository: AppRepository) : ViewModel() {

    fun searchTask(
        searchFilterText: String,
        workspaceId: Long,
        groupId: Long
    ): Flow<PagingData<WorkspaceGroupTaskModel>> {

        return Pager(
            PagingConfig(
                PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            appRepository.workspaceSearchTask(searchFilterText, workspaceId, groupId)
        }.flow
            .cachedIn(viewModelScope)
    }

    fun deleteTask(task: WorkspaceGroupTaskModel): Int {
        return appRepository.deleteWorkspaceTaskById(task)
    }

    fun undoTaskRemove(workspaceGroupTaskModel: WorkspaceGroupTaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.insertWorkspaceTodoTask(workspaceGroupTaskModel)
        }
    }

}
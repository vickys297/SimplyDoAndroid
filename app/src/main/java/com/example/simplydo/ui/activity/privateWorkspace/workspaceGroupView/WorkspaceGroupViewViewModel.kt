package com.example.simplydo.ui.activity.privateWorkspace.workspaceGroupView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.ui.activity.personalWorkspace.personalTask.PAGE_SIZE
import com.example.simplydo.utils.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkspaceGroupViewViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableArrayWorkspaceGroupModel = MutableLiveData<ArrayList<WorkspaceGroupModel>>()
    val mutableWorkspaceCount = MutableLiveData<Int>()

    fun getWorkspaceGroup(workspaceID: Long): Flow<PagingData<WorkspaceGroupModel>> {
        return Pager(
            PagingConfig(
                PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            appRepository.getWorkspaceGroup(workspaceID)
        }.flow
            .cachedIn(viewModelScope)
    }

    fun getWorkSpaceCount(workspaceID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = appRepository.getWorkspaceGroupTaskCount(workspaceID)
            mutableWorkspaceCount.postValue(count)
        }
    }

    fun deleteWorkspaceGroup(item: WorkspaceGroupModel) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.deleteWorkspaceGroup(item)
        }
    }
}
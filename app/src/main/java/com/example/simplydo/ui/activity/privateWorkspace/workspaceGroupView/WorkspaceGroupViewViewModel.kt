package com.example.simplydo.ui.activity.privateWorkspace.workspaceGroupView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkspaceGroupViewViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableArrayWorkspaceGroupModel = MutableLiveData<ArrayList<WorkspaceGroupModel>>()

    fun getWorkspaceGroup(workspaceID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = appRepository.getWorkspaceGroup(workspaceID)
            mutableArrayWorkspaceGroupModel.postValue(result)
        }
    }
}
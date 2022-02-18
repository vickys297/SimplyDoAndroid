package com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkspaceGroupTaskViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableWorkspaceGroupTask = MutableLiveData<ArrayList<WorkspaceGroupTaskModel>>()

    fun getWorkspaceGroupTask(groupTaskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepository.getWorkspaceTaskByGroupId(groupTaskId)
            mutableWorkspaceGroupTask.postValue(response)
        }
    }
}
package com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskListFullDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppRepository

class WorkspaceTaskFullDetailsViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableTodoDataSet = MutableLiveData<WorkspaceGroupTaskModel>()

    fun getTodoDataById(dtId: Long): WorkspaceGroupTaskModel {
        return appRepository.getWorkspaceTaskByTaskId(dtId)
    }

    fun updateTaskModel(todoModel: WorkspaceGroupTaskModel) {
        appRepository.updateWorkspaceTaskData(todoModel)
    }

    fun updateTodoData(it: WorkspaceGroupTaskModel) {
//        appRepository.updateWorkspaceTaskData(updateModel = it)
    }
}
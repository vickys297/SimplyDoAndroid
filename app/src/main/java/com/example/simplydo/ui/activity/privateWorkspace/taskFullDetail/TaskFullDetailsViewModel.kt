package com.example.simplydo.ui.activity.privateWorkspace.taskFullDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppRepository

class TaskFullDetailsViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableTodoDataSet = MutableLiveData<WorkspaceGroupTaskModel>()

    fun getTodoDataById(dtId: Long): WorkspaceGroupTaskModel {
        return appRepository.getWorkspaceTaskByTaskId(dtId)
    }

    fun updateTaskModel(workspaceGroupTaskModel: WorkspaceGroupTaskModel) {
        appRepository.updateWorkspaceTaskData(workspaceGroupTaskModel)
    }

    fun updateTodoData(workspaceGroupTaskModel: WorkspaceGroupTaskModel) {
//        appRepository.updateTodoData(updateModel = workspaceGroupTaskModel)
    }
}
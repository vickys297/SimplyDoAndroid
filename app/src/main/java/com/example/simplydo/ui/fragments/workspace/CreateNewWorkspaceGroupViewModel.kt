package com.example.simplydo.ui.fragments.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNewWorkspaceGroupViewModel(val appRepository: AppRepository) : ViewModel() {


    fun insertNewGroup(newGroup: WorkspaceGroupModel) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.insertNewWorkspaceGroup(newGroup)
        }
    }
}
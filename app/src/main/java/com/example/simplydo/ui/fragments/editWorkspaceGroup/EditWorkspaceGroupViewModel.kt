package com.example.simplydo.ui.fragments.editWorkspaceGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utils.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditWorkspaceGroupViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun updateWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.updateWorkspaceGroup(workspaceGroupModel)
        }
    }
}
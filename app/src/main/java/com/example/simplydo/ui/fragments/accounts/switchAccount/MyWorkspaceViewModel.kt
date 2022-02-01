package com.example.simplydo.ui.fragments.accounts.switchAccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MyWorkspaceViewModel(val appRepository: AppRepository) : ViewModel() {
    val mutableWorkspace = MutableLiveData<ArrayList<WorkspaceModel>>()

    fun getAvailableWorkspace() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = appRepository.getWorkspaceList()
            mutableWorkspace.postValue(data)
        }
    }
}
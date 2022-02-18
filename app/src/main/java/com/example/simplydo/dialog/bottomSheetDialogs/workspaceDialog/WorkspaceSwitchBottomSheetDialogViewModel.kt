package com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal val TAG = WorkspaceSwitchBottomSheetDialogViewModel::class.java.canonicalName

class WorkspaceSwitchBottomSheetDialogViewModel(val appRepository: AppRepository) : ViewModel() {
    val mutableWorkspaceModel = MutableLiveData<ArrayList<WorkspaceModel>>()

    fun getMyWorkspace() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepository.getWorkspaceList()
            Log.i(TAG, "getMyWorkspace: $response")
            mutableWorkspaceModel.postValue(response)
        }
    }
}
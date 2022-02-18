package com.example.simplydo.ui.activity.privateWorkspace.createWorkspace

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.UserIdModel
import com.example.simplydo.model.UserModel
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import com.example.simplydo.utlis.AppRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateWorkspaceViewModel(val appRepository: AppRepository) : ViewModel() {

    fun createNewWorkSpace(workspace: String, content: Context) {
        val data = WorkspaceModel(
            orgId = AppPreference.getPreferences(
                AppConstant.Preferences.ORGANIZATION_ID,
                -1L,
                content
            ),
            accountType = "PAID",
            title = workspace,
            moreDetails = "",
            createdBy = UserIdModel(
                admin = Gson().fromJson(
                    AppPreference.getPreferences(
                        AppConstant.Preferences.USER_DATA,
                        content
                    ), UserModel::class.java
                )
            ),
            users = ArrayList()
        )
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.createNewWorkspace(data)
        }
    }
}
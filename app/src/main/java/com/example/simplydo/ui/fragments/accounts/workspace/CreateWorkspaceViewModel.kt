package com.example.simplydo.ui.fragments.accounts.workspace

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.UserIdModel
import com.example.simplydo.model.UserModel
import com.example.simplydo.model.WorkspaceAccountModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import com.example.simplydo.utlis.AppRepository
import com.google.gson.Gson

class CreateWorkspaceViewModel(val appRepository: AppRepository) : ViewModel() {

    fun createNewWorkSpace(workspace: String, content: Context) {
        val data = WorkspaceAccountModel(
            orgId = AppPreference.getPreferences(AppConstant.Preferences.ORGANIZATION_ID, content),
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
            users = ArrayList(),
            groups = ArrayList()
        )

        appRepository.createNewWorkspace(data)


    }
}
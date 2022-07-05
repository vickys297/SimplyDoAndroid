package com.example.simplydo.api

import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.POST

interface WorkspaceAPI {

    @POST("workspace/group")
    fun syncWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel): Call<CommonResponseModel>
}
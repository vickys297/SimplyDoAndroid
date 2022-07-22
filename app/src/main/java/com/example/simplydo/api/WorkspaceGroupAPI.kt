package com.example.simplydo.api

import com.example.simplydo.model.workspaceGroup.WorkspaceGroupModel
import retrofit2.Response
import retrofit2.http.GET

interface WorkspaceGroupAPI {

    @GET("organization/workspace/group/people")
    suspend fun getPeopleList(): Response<WorkspaceGroupModel>

}
package com.example.simplydo.model

import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel

data class GroupTaskByProgressModel(
    val taskHeader: TaskHeaderContent? = null,
    val message: String? = null,
    val content: WorkspaceGroupTaskModel? = null
) {
    data class TaskHeaderContent(
        val title: String,
        val subtitle: String
    )
}

data class TaskStatusDataModel(
    val statusName: String,
    val statusId: Int,
    val statusColor: String
)
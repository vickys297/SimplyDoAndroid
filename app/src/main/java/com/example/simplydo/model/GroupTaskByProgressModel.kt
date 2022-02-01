package com.example.simplydo.model

data class GroupTaskByProgressModel(
    val taskHeader: TaskHeaderContent? = null,
    val content: WorkspaceGroupTaskModel? = null
) {
    data class TaskHeaderContent(
        val title: String,
        val subtitle: String
    )
}

data class TaskStatusDataModel(
    val statusName: String,
    val statusId: Int
)
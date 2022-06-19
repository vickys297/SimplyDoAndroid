package com.example.simplydo.ui.activity.personalWorkspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PersonalWorkspaceViewModel(val appRepository: AppRepository) : ViewModel() {

    fun getAllTodoListNotSynced(): LiveData<List<TodoModel>> {
        return appRepository.appDatabase.todoDao().getAllTodoNotSynced()
    }

    fun syncDataWithCloud(arrayList: ArrayList<TodoModel>) {
        appRepository.uploadDataToCloudDatabase(arrayList)
    }


    fun insertDummyDataIntoLocalDatabase(
        task: String,
        title: String,
        eventDate: Long,
        taskPriority: Int,
        isCompleted: Boolean = false,
        contactList: ArrayList<ContactModel>,
        imageList: ArrayList<GalleryModel>,
        taskType: Int
    ) {
        appRepository.insertTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                taskPriority = taskPriority,
                locationData = LatLngModel(),
                contactAttachments = contactList,
                galleryAttachments = imageList,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isCompleted = isCompleted,
                taskType = taskType
            )
        )

        appRepository.insertWorkspaceTodoTask(
            WorkspaceGroupTaskModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                taskPriority = taskPriority,
                locationData = LatLngModel(),
                contactAttachments = contactList,
                galleryAttachments = imageList,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                isCompleted = isCompleted,
                taskType = taskType,
                groupId = "0987654321",
                workspaceId = "1987654321"
            )
        )
    }

    fun getAvailableTagList(): ArrayList<TagModel> {
        return appRepository.getAvailableTags()
    }

    fun insertTag(tag: String) {
        appRepository.insertTag(tag)
    }

    fun storePrivateSpace(workspace: WorkspaceModel) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.writeNewWorkspace(workspace)
        }
    }

    fun createWorkspaceGroup(dataset: ArrayList<WorkspaceGroupModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.createWorkspaceGroup(dataset)
        }
    }
}
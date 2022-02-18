package com.example.simplydo.ui.fragments.addOrEditTodoTask

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.AppRepository
import java.util.*

class AddNewTodoViewModel(val appRepository: AppRepository) :
    ViewModel() {


    val selectedPriority = MutableLiveData<Int>()
    val selectedRepeatFrequency = MutableLiveData<ArrayList<SelectorDataModal>>()
    val selectedRepeatDays = MutableLiveData<ArrayList<SelectorDataModal>>()
    val selectedTags = MutableLiveData<ArrayList<TagModel>>()

    val arrayListTodoTask = MutableLiveData<ArrayList<TodoTaskModel>>()


    fun createTodo(
        title: String,
        task: String,
        eventDate: Long,
        taskPriority: Int,
        contactArray: ArrayList<ContactModel>,
        galleryArray: ArrayList<GalleryModel>,
        audioArray: ArrayList<AudioModel>,
        location: LatLngModel,
        filesArray: ArrayList<FileModel>,
        repeatFrequency: ArrayList<SelectorDataModal>,
        repeatWeek: ArrayList<SelectorDataModal>
    ): Long {
        return appRepository.insertTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                taskPriority = taskPriority,
                locationData = location,
                contactAttachments = contactArray,
                galleryAttachments = galleryArray,
                audioAttachments = audioArray,
                fileAttachments = filesArray,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                repeatFrequency = repeatFrequency,
                repeatDays = repeatWeek
            )
        )
    }


    fun createWorkspaceTask(
        title: String,
        task: String,
        eventDate: Long,
        taskPriority: Int,
        galleryArray: ArrayList<GalleryModel>,
        contactArray: ArrayList<ContactModel>,
        audioArray: ArrayList<AudioModel>,
        filesArray: ArrayList<FileModel>,
        location: LatLngModel,
        repeatFrequency: ArrayList<SelectorDataModal>,
        repeatWeek: ArrayList<SelectorDataModal>,
        workspaceId: Long,
        workspaceGroupId: Long
    ): Long {
        return appRepository.insertWorkspaceTodoTask(
            WorkspaceGroupTaskModel(
                title = title,
                todo = task,
                groupId = workspaceGroupId.toString(),
                workspaceId = workspaceId.toString(),
                eventDateTime = eventDate,
                taskPriority = taskPriority,
                locationData = location,
                contactAttachments = contactArray,
                galleryAttachments = galleryArray,
                audioAttachments = audioArray,
                fileAttachments = filesArray,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                repeatFrequency = repeatFrequency,
                repeatDays = repeatWeek
            )
        )
    }

    fun updateTodo(
        dtId: Long,
        title: String,
        task: String,
        eventDate: Long,
        taskPriority: Int,
        galleryArray: ArrayList<GalleryModel>,
        contactArray: ArrayList<ContactModel>,
        audioArray: ArrayList<AudioModel>,
        filesArray: ArrayList<FileModel>,
        location: LatLngModel,
        createAt: String,
        repeatFrequency: ArrayList<SelectorDataModal>,
        repeatWeek: ArrayList<SelectorDataModal>,
        taskType: Int,
        taskTags: ArrayList<TagModel>,
        arrayListTodoTask: ArrayList<TodoTaskModel>
    ): Int {
       val updateModel =  TodoModel(
           dtId = dtId,
           title = title,
           todo = task,
           eventDateTime = eventDate,
           taskPriority = taskPriority,
           contactAttachments = contactArray,
           locationData = location,
           galleryAttachments = galleryArray,
           audioAttachments = audioArray,
           fileAttachments = filesArray,
           createdAt = createAt,
           updatedAt = System.currentTimeMillis().toString(),
           repeatFrequency = repeatFrequency,
           repeatDays = repeatWeek,
           taskType = taskType,
           taskTags = taskTags,
           arrayListTodoTask = arrayListTodoTask
       )

        Log.i(TAG, "updateTodo: $updateModel")

        return appRepository.updateTodoData(updateModel)
    }

}
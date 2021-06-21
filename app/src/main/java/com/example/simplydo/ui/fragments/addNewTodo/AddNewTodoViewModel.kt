package com.example.simplydo.ui.fragments.addNewTodo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AppRepository
import java.util.*

class AddNewTodoViewModel(val appRepository: AppRepository) :
    ViewModel() {
    fun createTodo(
        title: String,
        task: String,
        eventDate: Long,
        eventTime: String,
        priority: Boolean,
        contactArray: ArrayList<ContactModel>,
        galleryArray: ArrayList<GalleryModel>,
        audioArray: ArrayList<AudioModel>,
        location: LatLngModel,
        filesArray: ArrayList<FileModel>
    ): Long {
        return appRepository.reinsertTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDateTime = eventDate,
                isHighPriority = priority,
                contactAttachments = contactArray,
                locationData = location,
                imageAttachments = galleryArray,
                audioAttachments = audioArray,
                fileAttachments = filesArray,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
            )
        )
    }

    fun updateTodo(
        title: String,
        task: String,
        eventDate: Long,
        eventTime: String,
        priority: Boolean,
        galleryArray: ArrayList<GalleryModel>,
        contactArray: ArrayList<ContactModel>,
        audioArray: ArrayList<AudioModel>,
        filesArray: ArrayList<FileModel>,
        location: LatLngModel,
        createAt: String
    ): Int {
       val updateModel =  TodoModel(
            title = title,
            todo = task,
            eventDateTime = eventDate,
            isHighPriority = priority,
            contactAttachments = contactArray,
            locationData = location,
            imageAttachments = galleryArray,
            audioAttachments = audioArray,
            fileAttachments = filesArray,
            createdAt = createAt,
            updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                .format(Date().time)
        )

        Log.i(TAG, "updateTodo: $updateModel")

        return appRepository.updateTodoData(updateModel)
    }
}
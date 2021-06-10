package com.example.simplydo.ui.fragments.addNewTodo

import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
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
        audioArray: ArrayList<AudioModel>
    ) {
        appRepository.insertNewTodoTask(
            TodoModel(
                title = title,
                todo = task,
                eventDate = eventDate,
                eventTime = eventTime,
                isHighPriority = priority,
                contactAttachments = contactArray,
                locationData = "",
                imageAttachments = galleryArray,
                audioAttachments = audioArray,
                createdAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
                updatedAt = AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_ISO)
                    .format(Date().time),
            )
        )
    }
}
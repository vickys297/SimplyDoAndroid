package com.example.simplydo.utli

import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel

interface CreateBasicTodoInterface {
    fun onAddMoreDetails(eventDate: Long)
    fun onCreateTodo(
        title: String,
        task: String,
        eventDate: Long,
        eventTime: String,
        isPriority: Boolean
    )
}

interface CalenderAdapterInterface {
    fun onDateSelect(layoutPosition: Int, smallCalenderModel: SmallCalenderModel)
}

interface TodoItemInterface {
    fun onLongClick(item: TodoModel)
    fun onTaskClick(item: TodoModel, absoluteAdapterPosition: Int)
}

interface AddAttachmentInterface {
    fun onAddDocument()
    fun onAddAudio()
    fun onOpenGallery()
    fun onAddContact()
    fun onAddLocation()
    fun onCancelTask()
}

interface ContactAdapterInterface {
    fun onContactSelect(item: ContactModel)
}

interface SelectedContactInterface {
    fun onContactRemove(item: ContactModel)
}

interface CommonBottomSheetDialogInterface {
    fun onPositiveButtonClick()
}


interface NewTodoOptionsFragmentsInterface {
    fun onAddAttachments()
    fun onClose()
}

interface TodoOptionDialogFragments {
    fun onDelete(item: TodoModel)
    fun onEdit(item: TodoModel)
    fun onRestore(item: TodoModel)
}

interface AudioInterface {
    fun onPlay(audioModel: AudioModel)
    fun onAudioSelect(audioModel: AudioModel)
}

interface GalleryInterface {
    fun onGallerySelect(galleryModel: GalleryModel)
    fun onViewItem(galleryModel: GalleryModel)
}

interface TodoTaskOptionsInterface {
    fun onCalenderView()
    fun onCompletedView()
    fun onPastTaskView()
}


interface GalleryAttachmentInterface {
    fun onItemSelect(item: GalleryModel)
}

interface AudioAttachmentInterface {
    fun onAudioSelect(item: AudioModel)
}
interface ContactAttachmentInterface {
    fun onContactSelect(item: ContactModel)
}
interface FileAttachmentInterface {
    fun onFileSelect(item: FileModel)
}
package com.example.simplydo.utli

import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.GalleryModel

interface CreateBasicTodoInterface {
    fun onAddMoreDetails(eventDate: String)
    fun onCreateTodo(title: String, task: String, eventDate: String, isPriority: Boolean)
}

interface CalenderAdapterInterface {
    fun onDateSelect(layoutPosition: Int, dateEvent: String)
}

interface TodoAdapterInterface {
    fun onLongClick(item: TodoModel)
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

interface GalleryInterface{
    fun onGallerySelect(galleryModel: GalleryModel)
    fun onViewItem(galleryModel: GalleryModel)
}

interface TodoTaskOptionsInterface {
    fun onCalenderView()
    fun onCompletedView()
    fun onPastTaskView()
}
package com.example.simplydo.utli

import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel

interface CreateBasicTodoInterface {
    fun onAddMoreDetails(eventDate: String)
    fun onCreateTodo(title: String, task: String, eventDate: String, isPriority: Boolean)
}

interface CalenderAdapterInterface {
    fun onDateSelect(position: Int, dateEvent: String)
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
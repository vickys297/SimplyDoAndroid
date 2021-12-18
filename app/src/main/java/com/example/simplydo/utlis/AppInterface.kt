package com.example.simplydo.utlis

import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel

interface NewRemainderInterface {
    fun onAddMoreDetails(eventDate: Long)
    fun onCreateTodo(
        title: String,
        task: String,
        eventDate: Long,
        isPriority: Boolean,
        isAllDayTask: Boolean = true
    )
}

interface EditBasicTodoInterface {
    fun onUpdateDetails(todoModel: TodoModel)
    fun onAddMoreDetails(todoModel: TodoModel)
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
    fun onRemoveItem(position: Int)
}

interface ContactAttachmentInterface {
    fun onContactSelect(item: ContactModel)
}

interface FileAttachmentInterface {
    fun onFileSelect(item: FileModel)
}

interface UndoInterface {
    fun onUndo(task: TodoModel, type: Int)
}

interface Page4Interface {
    fun onStart()
}

interface RepeatDialogInterface {
    fun onSetRepeat(
        arrayFrequency: ArrayList<SelectorDataModal>,
        arrayWeek: ArrayList<SelectorDataModal>
    )

    fun onCancel()
}


interface CommonSelector {
    fun onWeekSelected(arrayList: ArrayList<SelectorDataModal>)
    fun onPeriodSelected(arrayList: ArrayList<SelectorDataModal>)
}

interface NewTodo {
    interface AttachmentInterface {
        fun onRemove()
        fun onClick()
    }

    interface AddTask {
        fun onAddText()
        fun onAddList()
        fun onTaskRemove(item: TodoTaskModel, position: Int)
    }

    interface TodoTask {
        fun onTaskSelect(item: TodoTaskModel)
    }
}


interface AppInterface {
    interface TaskNoteTextItemListener {
        fun onAdd(content: String)
    }

    interface NewTaskDialogCallback {
        fun onOptionSelected(option: Int)
        fun onClose()
    }

    interface TagDialog {
        interface Callback {
            fun onDone(selectedTag: ArrayList<TagModel>)
        }
    }

    interface PriorityDialog{
        interface Callback{
            fun onSelect(priority: Int) // 1 - high, 2 - Medium, 3 - low
        }
    }
}


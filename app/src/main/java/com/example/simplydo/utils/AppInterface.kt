package com.example.simplydo.utils

import android.view.View
import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel

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

interface EditBasicWorkspaceTaskInterface {
    fun onUpdateDetails(todoModel: WorkspaceGroupTaskModel)
    fun onAddMoreDetails(todoModel: WorkspaceGroupTaskModel)
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

interface SnoozeTimeInterface {
    fun onSnoozeTimeSelect(time: Int)
    fun onClose()
}

interface SelectedContactInterface {
    fun onContactRemove(item: ContactModel)
}

interface CommonBottomSheetDialogInterface {
    fun onPositiveButtonClick(content: Any)
    fun onNegativeButtonClick(content: Any)
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
    fun onSettingsClicked()
}


interface GalleryAttachmentInterface {
    fun onItemSelect(item: GalleryModel, indexOf: Int)
    fun onItemRemoved(removedItem: GalleryModel, indexOf: Int)
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
    fun onUndo(task: Any, type: Int)
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

interface ParticipantInterface {
    fun onParticipantSelected(item: UserAccountModel)
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
        fun onCompleted(item: TodoTaskModel)
    }
}

interface PaidPlain {
    interface Callback {
        fun onSelectPlan(id: Int)
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

    interface PriorityDialog {
        interface Callback {
            fun onSelect(priority: Int) // 1 - high, 2 - Medium, 3 - low
        }
    }


    interface TaskFullDetailsCallBack {
        fun onDelete()
        fun onViewCalendar()
        fun onShare()
        fun onStageSelect(item: TaskStatusDataModel)
        fun onAddParticipants()

        interface TaskFullDetailsStageCallback {
            fun onStageSelected(item: TaskStatusDataModel)
        }
    }

    interface GroupViewCallback {
        fun onSelect(item: WorkspaceGroupModel)
        fun onOption(item: WorkspaceGroupModel)
    }

    interface GroupViewOptionCallback {
        fun onEdit(item: WorkspaceGroupModel)
        fun onDelete(item: WorkspaceGroupModel)
    }

    interface WorkspaceAdapter {
        interface Callback {
            fun onWorkSpaceSelected(item: LinkedWorkspaceDataModel)
        }
    }

    interface MyWorkspace {
        interface Callback {
            fun onSelect(item: WorkspaceModel)
        }

        interface CreateWorkspaceDialog {
            fun onCreateNewWorkspace()
        }
    }

    interface WorkspaceGroupTask {
        interface Task {
            fun onTaskSelected(content: WorkspaceGroupTaskModel)
            fun onTaskDeleted(content: WorkspaceGroupTaskModel)
        }
    }

    interface StickyHeaderInterface {
        /**
         * This method gets called by [StickHeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to setup the header View.
         * @param header View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View?, headerPosition: Int)

        /**
         * This method gets called by [StickHeaderItemDecoration] to verify whether the item represents a header.
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }
}


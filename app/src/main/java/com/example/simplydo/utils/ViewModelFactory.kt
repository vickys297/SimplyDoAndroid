package com.example.simplydo.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.dialog.bottomSheetDialogs.tags.TagsBottomSheetDialogViewModel
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.WorkspaceSwitchBottomSheetDialogViewModel
import com.example.simplydo.ui.activity.personalWorkspace.PersonalWorkspaceViewModel
import com.example.simplydo.ui.activity.personalWorkspace.personalTask.PersonalWorkspaceTaskViewViewModel
import com.example.simplydo.ui.activity.privateWorkspace.createWorkspace.CreateWorkspaceViewModel
import com.example.simplydo.ui.activity.privateWorkspace.createWorkspaceGroup.CreateNewWorkspaceGroupViewModel
import com.example.simplydo.ui.activity.privateWorkspace.editTaskDetails.EditWorkspaceTaskViewModel
import com.example.simplydo.ui.activity.privateWorkspace.workspaceGroupView.WorkspaceGroupViewViewModel
import com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskListFullDetails.WorkspaceTaskFullDetailsViewModel
import com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskView.WorkspaceGroupTaskViewModel
import com.example.simplydo.ui.fragments.accounts.myAccount.MyAccountsViewModel
import com.example.simplydo.ui.fragments.accounts.notifications.NotificationSettingsViewModel
import com.example.simplydo.ui.fragments.accounts.switchAccount.MyWorkspaceViewModel
import com.example.simplydo.ui.fragments.addOrEditTodoTask.AddNewTodoViewModel
import com.example.simplydo.ui.fragments.attachmentsFragments.contacts.ContactsViewModel
import com.example.simplydo.ui.fragments.attachmentsFragments.gallery.GalleryListViewModel
import com.example.simplydo.ui.fragments.calender.CalenderViewModel
import com.example.simplydo.ui.fragments.otherTodoFragments.OtherTodoViewModel
import com.example.simplydo.ui.fragments.searchTask.SearchTaskViewModel
import com.example.simplydo.ui.fragments.selectParticipants.SelectParticipantsViewModel
import com.example.simplydo.ui.fragments.todoListFullDetails.TodoFullDetailsViewModel

open class ViewModelFactory internal constructor(
    private val context: Context,
    private val appRepository: AppRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass.canonicalName) {
            PersonalWorkspaceTaskViewViewModel::class.java.canonicalName -> {
                PersonalWorkspaceTaskViewViewModel(this.appRepository) as T
            }

            AddNewTodoViewModel::class.java.canonicalName -> {
                AddNewTodoViewModel(
                    this.appRepository
                ) as T
            }

            CalenderViewModel::class.java.canonicalName -> {
                CalenderViewModel(this.appRepository) as T
            }

            OtherTodoViewModel::class.java.canonicalName -> {
                OtherTodoViewModel(this.appRepository) as T
            }

            ContactsViewModel::class.java.canonicalName -> {
                ContactsViewModel(appRepository) as T
            }

            PersonalWorkspaceViewModel::class.java.canonicalName -> {
                PersonalWorkspaceViewModel(appRepository) as T
            }

            TagsBottomSheetDialogViewModel::class.java.canonicalName -> {
                TagsBottomSheetDialogViewModel(appRepository) as T
            }

            CreateWorkspaceViewModel::class.java.canonicalName -> {
                CreateWorkspaceViewModel(appRepository) as T
            }
            MyWorkspaceViewModel::class.java.canonicalName -> {
                MyWorkspaceViewModel(appRepository) as T
            }
            WorkspaceGroupViewViewModel::class.java.canonicalName -> {
                WorkspaceGroupViewViewModel(appRepository) as T
            }

            SelectParticipantsViewModel::class.java.canonicalName -> {
                SelectParticipantsViewModel(appRepository) as T
            }
            CreateNewWorkspaceGroupViewModel::class.java.canonicalName -> {
                CreateNewWorkspaceGroupViewModel(appRepository) as T
            }
            WorkspaceGroupTaskViewModel::class.java.canonicalName -> {
                WorkspaceGroupTaskViewModel(appRepository) as T
            }
            WorkspaceSwitchBottomSheetDialogViewModel::class.java.canonicalName -> {
                WorkspaceSwitchBottomSheetDialogViewModel(appRepository) as T
            }

            TodoFullDetailsViewModel::class.java.canonicalName -> {
                TodoFullDetailsViewModel(appRepository) as T
            }

            WorkspaceTaskFullDetailsViewModel::class.java.canonicalName -> {
                WorkspaceTaskFullDetailsViewModel(appRepository) as T
            }

            EditWorkspaceTaskViewModel::class.java.canonicalName -> {
                EditWorkspaceTaskViewModel(appRepository) as T
            }

            MyAccountsViewModel::class.java.canonicalName -> {
                MyAccountsViewModel(appRepository) as T
            }

            SearchTaskViewModel::class.java.canonicalName -> {
                SearchTaskViewModel(appRepository) as T
            }

            NotificationSettingsViewModel::class.java.canonicalName -> {
                NotificationSettingsViewModel(appRepository) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }


}

class SimpleViewModelFactory internal constructor(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass.canonicalName) {
            GalleryListViewModel::class.java.canonicalName -> {
                GalleryListViewModel(context) as T
            }

            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }
}
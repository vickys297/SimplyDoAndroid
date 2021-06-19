package com.example.simplydo.utli

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.ui.MainViewModel
import com.example.simplydo.ui.fragments.addNewTodo.AddNewTodoViewModel
import com.example.simplydo.ui.fragments.attachmentsFragments.contact.ContactsViewModel
import com.example.simplydo.ui.fragments.attachmentsFragments.gallery.GalleryListViewModel
import com.example.simplydo.ui.fragments.calender.CalenderViewModel
import com.example.simplydo.ui.fragments.otherTodoFragments.OtherTodoViewModel
import com.example.simplydo.ui.fragments.quickTodoList.QuickTodoViewModel
import com.example.simplydo.ui.fragments.todoListFullDetails.TodoFullDetailsViewModel

open class ViewModelFactory internal constructor(
    private val context: Context,
    private val appRepository: AppRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when (modelClass.canonicalName) {
            QuickTodoViewModel::class.java.canonicalName -> {
                QuickTodoViewModel(this.appRepository) as T
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

            MainViewModel::class.java.canonicalName -> {
                MainViewModel(appRepository) as T
            }

            TodoFullDetailsViewModel::class.java.canonicalName->{
                TodoFullDetailsViewModel(appRepository) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }

}

open class SimpleViewModelFactory internal constructor(
    private val context: Context,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when (modelClass.canonicalName) {
            GalleryListViewModel::class.java.canonicalName->{
                GalleryListViewModel(context) as T
            }

            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }

}
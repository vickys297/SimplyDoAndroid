package com.example.simplydo.utli

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.ui.fragments.addNewTodo.AddNewTodoViewModel
import com.example.simplydo.ui.fragments.calender.CalenderViewModel
import com.example.simplydo.ui.fragments.contactListView.ContactsViewModel
import com.example.simplydo.ui.fragments.todoList.ToDoViewModel

open class ViewModelFactory internal constructor(
    private val context: Context,
    private val appRepository: AppRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when (modelClass.canonicalName) {
            ToDoViewModel::class.java.canonicalName -> {
                ToDoViewModel(context, this.appRepository) as T
            }
            AddNewTodoViewModel::class.java.canonicalName -> {
                AddNewTodoViewModel(
                    context,
                    this.appRepository
                ) as T
            }
            CalenderViewModel::class.java.canonicalName -> {
                CalenderViewModel(this.appRepository) as T
            }

            ContactsViewModel::class.java.canonicalName->{
                ContactsViewModel(context) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }

}
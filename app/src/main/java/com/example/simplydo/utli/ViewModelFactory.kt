package com.example.simplydo.utli

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.screens.todoList.ToDoViewModel
import com.example.simplydo.screens.todoList.addNewTodo.AddNewTodoViewModel

open class ViewModelFactory internal constructor(
    private val context: Context,
    private val repository: Repository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when (modelClass.canonicalName) {
            ToDoViewModel::class.java.canonicalName -> {
                ToDoViewModel(context, this.repository) as T
            }
            AddNewTodoViewModel::class.java.canonicalName -> {
                AddNewTodoViewModel(
                    context,
                    this.repository
                ) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }

}
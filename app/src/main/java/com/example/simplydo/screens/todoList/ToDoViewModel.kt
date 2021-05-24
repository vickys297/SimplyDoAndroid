package com.example.simplydo.screens.todoList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TodoList
import com.example.simplydo.utli.Repository


class ToDoViewModel(private val context: Context, private val repository: Repository) :
    ViewModel() {

    // TODO: Implement the ViewModel

    val todoList = MutableLiveData<ArrayList<TodoList>>()


    fun todoListObserver(): LiveData<List<TodoList>> {
        return repository.appDatabase.todoDao().getAllTodo()
    }

    fun getTodoList(): ArrayList<TodoList> {
        return repository.getTodoList()
    }

    fun removeTaskById(id: Int) {
        return repository.deleteTaskByPosition(id)
    }

}
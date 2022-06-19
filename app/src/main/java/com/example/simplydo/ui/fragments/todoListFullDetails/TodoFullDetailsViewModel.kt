package com.example.simplydo.ui.fragments.todoListFullDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utils.AppRepository

class TodoFullDetailsViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableTodoDataSet = MutableLiveData<TodoModel>()

    fun getTodoDataById(dtId: Long): TodoModel {
        return appRepository.getTodoById(dtId)
    }

    fun updateTaskModel(todoModel: TodoModel) {
        appRepository.updateTodoData(todoModel)
    }

    fun updateTodoData(it: TodoModel) {
        appRepository.updateTodoData(updateModel = it)
    }
}
package com.example.simplydo.ui.fragments.todoListFullDetails

import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.AppRepository

class TodoFullDetailsViewModel(val appRepository: AppRepository) : ViewModel() {

    fun getTodoDataById(dtId: Long): TodoModel {
        return appRepository.getTodoById(dtId)
    }

    fun updateTaskModel(todoModel: TodoModel) {
        appRepository.updateTodoData(todoModel)
    }
}
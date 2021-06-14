package com.example.simplydo.ui.fragments.todoListFullDetails

import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppRepository

class TodoFullDetailsViewModel(val appRepository: AppRepository) : ViewModel() {



    fun getTodoDataById(dtId: Long): TodoModel {
       return appRepository.getTodoById(dtId)
    }
    // TODO: Implement the ViewModel
}
package com.example.simplydo.screens.calender

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.Repository

class CalenderViewModel(private val context: Context, val repository: Repository) : ViewModel() {


    var todoList = MutableLiveData<List<TodoModel>>()

    fun getTodoListByEventDate(date: String) {
        repository.getTodoByEventDate(date, todoList)
    }



}
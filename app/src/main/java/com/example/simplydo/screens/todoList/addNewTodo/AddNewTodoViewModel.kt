package com.example.simplydo.screens.todoList.addNewTodo

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoList
import com.example.simplydo.utli.Repository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddNewTodoViewModel(private val context: Context,private val repository: Repository) : ViewModel() {

    // TODO: Implement the ViewModel
    fun insertIntoLocalDatabase(
        title: String,
        task: String,
        dateTime: String,
        contactInfo: ArrayList<ContactInfo>,
        imagesList: ArrayList<String>
    ) {

        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        repository.insertNewTodoTask(
            TodoList(
                title = title,
                todo = task,
                eventTime = dateTime,
                eventDate = dateTime,
                contactInfo = contactInfo,
                imageFiles = imagesList,
                locationInfo = "",
                createdAt = df1.format(Date()),
                updatedAt = df1.format(Date())
            ))
    }

}
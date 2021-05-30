package com.example.simplydo.ui.fragments.addNewTodo

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddNewTodoViewModel(private val context: Context,private val appRepository: AppRepository) : ViewModel() {

    // TODO: Implement the ViewModel
    fun insertIntoLocalDatabase(
        title: String,
        task: String,
        dateTime: String,
        contactInfo: ArrayList<ContactModel>,
        imagesList: ArrayList<String>
    ) {

        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        appRepository.insertNewTodoTask(
            TodoModel(
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
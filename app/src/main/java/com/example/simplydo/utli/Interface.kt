package com.example.simplydo.utli

import com.example.simplydo.model.TodoModel

interface CreateBasicTodoInterface {
    fun onAddMoreDetails(eventDate: String)
    fun onCreateTodo(title: String, task: String, eventDate: String, isPriority: Boolean)
}

interface CalenderAdapterInterface {
    fun onDateSelect(position: Int, dateEvent: String)
}

interface TodoAdapterInterface{
    fun onLongClick(item: TodoModel)
}
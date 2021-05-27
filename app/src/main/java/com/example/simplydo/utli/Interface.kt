package com.example.simplydo.utli

interface CreateBasicTodoInterface {
    fun onAddMoreDetails()
    fun onCreateTodo(title: String, task: String, date: String, checked: Boolean)
}

interface CalenderAdapterInterface {
    fun onDateSelect(position: Int, dateEvent: String)
}
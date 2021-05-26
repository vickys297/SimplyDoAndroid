package com.example.simplydo.utli

interface AppInterface {
    fun onAddMoreDetails()
    fun onCreateTodo(title: String, task: String, date: String, checked: Boolean)
}
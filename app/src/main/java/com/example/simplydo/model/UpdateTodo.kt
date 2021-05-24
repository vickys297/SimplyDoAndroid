package com.example.simplydo.model

data class UpdateTodo(
    val id: Int,
    val todo: String,
    val locationInfo: String,
    val eventDate: String,
    val eventTime: String,
    val contactInfo: String
)

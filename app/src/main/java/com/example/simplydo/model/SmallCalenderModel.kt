package com.example.simplydo.model

import java.io.Serializable

data class SmallCalenderModel(
    val id: Int = 0,
    val dateOfMonth: String,
    val month: String,
    val date: String,
    var isActive: Boolean = false,
    val startEventDate: Long,
    val endEventDate: Long,
) : Serializable

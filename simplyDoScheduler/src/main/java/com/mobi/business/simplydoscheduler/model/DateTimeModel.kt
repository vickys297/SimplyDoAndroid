package com.mobi.business.simplydoscheduler.model

data class DateTimeModel(
    val date: DayModel,
    val time: TimeModel,
)

data class DayModel(
    val day: Int,
    val month: Int,
    val year: Int,
)

data class TimeModel(
    val hours: Int,
    val minutes: Int,
)

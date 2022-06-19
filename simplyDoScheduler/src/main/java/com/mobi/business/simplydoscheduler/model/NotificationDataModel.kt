package com.mobi.business.simplydoscheduler.model

data class NotificationDataModel(
    val title: String,
    val message: String,
    val dateTime: DateTimeModel,
    val id: Int,
    val repeatable: Set<Int>,
    val repeatableType: Int,
) {

}

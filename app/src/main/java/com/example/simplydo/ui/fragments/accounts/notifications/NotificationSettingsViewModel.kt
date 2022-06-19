package com.example.simplydo.ui.fragments.accounts.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TaskReminderTime
import com.example.simplydo.utils.AppRepository

internal val TAG = NotificationSettingsViewModel::class.java.canonicalName

class NotificationSettingsViewModel(val appRepository: AppRepository) : ViewModel() {

    // get application context
    private fun getApplicationContext() = appRepository.getApplicationContext()

    val mutableTaskReminderTime: MutableLiveData<TaskReminderTime> = MutableLiveData()
    val mutableEveryDayTaskReminderTime: MutableLiveData<TaskReminderTime> = MutableLiveData()

}
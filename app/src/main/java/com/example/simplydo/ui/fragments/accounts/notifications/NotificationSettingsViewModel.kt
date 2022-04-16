package com.example.simplydo.ui.fragments.accounts.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.utlis.AppRepository

internal val TAG = NotificationSettingsViewModel::class.java.canonicalName

class NotificationSettingsViewModel(val appRepository: AppRepository) : ViewModel() {

    // get application context
    private fun getApplicationContext() = appRepository.getApplicationContext()

    val mutableRemindMeTime: MutableLiveData<Long> = MutableLiveData(1649961900179)


}
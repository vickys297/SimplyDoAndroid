package com.example.simplydo.ui.fragments.selectParticipants

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.AccountModel
import com.example.simplydo.utils.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectParticipantsViewModel(val appRepository: AppRepository) : ViewModel() {

    val mutableParticipantsData = MutableLiveData<ArrayList<AccountModel>>()

    fun getParticipantsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val dataset = appRepository.getParticipatesFromWorkspace()
            mutableParticipantsData.postValue(dataset)
        }
    }
}
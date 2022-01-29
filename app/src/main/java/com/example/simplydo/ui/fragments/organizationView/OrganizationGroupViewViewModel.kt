package com.example.simplydo.ui.fragments.organizationView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrganizationGroupViewViewModel(val appRepository: AppRepository) : ViewModel() {
    val mutableArrayTodoModel = MutableLiveData<ArrayList<TodoModel>>()
    fun getDummyTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = appRepository.getDummyTask()
            mutableArrayTodoModel.postValue(result)
        }
    }
}
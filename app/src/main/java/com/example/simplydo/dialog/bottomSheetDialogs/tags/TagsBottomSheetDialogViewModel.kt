package com.example.simplydo.dialog.bottomSheetDialogs.tags

import androidx.lifecycle.ViewModel
import com.example.simplydo.model.TagModel
import com.example.simplydo.utils.AppRepository
import java.util.*

class TagsBottomSheetDialogViewModel(val appRepository: AppRepository) : ViewModel() {

    fun getAvailableTags(): ArrayList<TagModel> {
        return appRepository.getAvailableTags()
    }


}
package com.example.simplydo.ui.fragments.attachmentsFragments.audio

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.dataSource.DataSourceAudio
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utlis.AppConstant
import kotlinx.coroutines.flow.Flow

class AudioListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val audioList = MutableLiveData<ArrayList<AudioModel>>()

    @SuppressLint("InlinedApi")
    fun getAudioList(context: Context): Flow<PagingData<AudioModel>> {
        return Pager(
            PagingConfig(pageSize = AppConstant.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
        ){
            DataSourceAudio(context)
        }.flow
            .cachedIn(viewModelScope)
    }
}
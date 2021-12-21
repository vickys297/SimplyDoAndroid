package com.example.simplydo.ui.fragments.attachmentsFragments.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.dataSource.GalleryDataSource
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.AppConstant
import kotlinx.coroutines.flow.Flow

internal val TAG = GalleryListViewModel::class.java.canonicalName

class GalleryListViewModel(context: Context) : ViewModel() {
    // TODO: Implement the ViewModel


    private val flow: Flow<PagingData<GalleryModel>>

    init {
        Log.i(TAG, "GalleryListViewModel: init")
        flow = Pager(
            PagingConfig(
                pageSize = AppConstant.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            GalleryDataSource(context)
        }.flow
            .cachedIn(viewModelScope)
    }


    fun getGalleryDataSource(): Flow<PagingData<GalleryModel>> {
        return flow
    }

    fun hasDataSourceValue(){

    }


}
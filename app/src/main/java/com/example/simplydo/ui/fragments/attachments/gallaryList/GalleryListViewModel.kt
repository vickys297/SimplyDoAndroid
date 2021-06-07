package com.example.simplydo.ui.fragments.attachments.gallaryList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.simplydo.utli.dataSource.GalleryDataSource

internal val TAG = GalleryListViewModel::class.java.canonicalName
class GalleryListViewModel(context: Context) : ViewModel() {
    // TODO: Implement the ViewModel


    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 30,
            prefetchDistance = 30,
            enablePlaceholders = false,
            initialLoadSize = 30)
    ) {
        GalleryDataSource(context)
    }.flow
        .cachedIn(viewModelScope)


}
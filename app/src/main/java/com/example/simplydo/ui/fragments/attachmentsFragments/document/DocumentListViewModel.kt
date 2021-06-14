package com.example.simplydo.ui.fragments.attachmentsFragments.document

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.dataSource.DataSourceDocument
import com.example.simplydo.model.attachmentModel.DocumentModel
import com.example.simplydo.utli.AppConstant
import kotlinx.coroutines.flow.Flow

class DocumentListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    fun getDocument(activity: FragmentActivity): Flow<PagingData<DocumentModel>> {
        return Pager(
            PagingConfig(
                pageSize = AppConstant.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            DataSourceDocument(activity)
        }.flow
            .cachedIn(viewModelScope)
    }
}
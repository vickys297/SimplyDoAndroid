package com.example.simplydo.ui.fragments.attachmentsFragments.contact

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.dataSource.DataSourceContactNew
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utlis.AppConstant.DEFAULT_PAGE_SIZE
import com.example.simplydo.utlis.AppRepository
import kotlinx.coroutines.flow.Flow

internal val ContactsViewModelTAG = ContactsViewModel::class.java.canonicalName

class ContactsViewModel(val appRepository: AppRepository) : ViewModel() {


    fun getContactList(context: Context): Flow<PagingData<ContactModel>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            DataSourceContactNew(context)
        }.flow
            .cachedIn(viewModelScope)
    }
}
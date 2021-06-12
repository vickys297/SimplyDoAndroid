package com.example.simplydo.ui.fragments.attachmentsFragments.contactList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.dataSource.ContactPagingSource
import kotlinx.coroutines.flow.Flow

internal val ContactsViewModelTAG = ContactsViewModel::class.java.canonicalName

class ContactsViewModel(val appRepository: AppRepository) : ViewModel() {


    fun getContactList(context: Context): Flow<PagingData<ContactModel>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            )
        ) {
            ContactPagingSource(context)
        }.flow
            .cachedIn(viewModelScope)
    }
}
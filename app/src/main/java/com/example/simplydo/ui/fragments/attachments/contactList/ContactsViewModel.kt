package com.example.simplydo.ui.fragments.attachments.contactList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.simplydo.utli.dataSource.ContactPagingSource

internal val ContactsViewModelTAG = ContactsViewModel::class.java.canonicalName

class ContactsViewModel(val context: Context) : ViewModel() {


    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 30,
            prefetchDistance = 30,
            enablePlaceholders = false,
            initialLoadSize = 30)
    ) {
        ContactPagingSource(context)
    }.flow
        .cachedIn(viewModelScope)
}
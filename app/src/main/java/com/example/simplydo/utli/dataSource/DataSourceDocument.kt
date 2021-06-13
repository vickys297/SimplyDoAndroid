package com.example.simplydo.utli.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.attachmentModel.DocumentModel

class DataSourceDocument :PagingSource<Int,DocumentModel>(){
    override fun getRefreshKey(state: PagingState<Int, DocumentModel>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DocumentModel> {
        TODO("Not yet implemented")
    }
}
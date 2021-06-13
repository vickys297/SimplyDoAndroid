package com.example.simplydo.utli.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.attachmentModel.AudioModel

class DataSourceAudio: PagingSource<Int, AudioModel>() {
    override fun getRefreshKey(state: PagingState<Int, AudioModel>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AudioModel> {
        TODO("Not yet implemented")
    }
}
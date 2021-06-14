package com.example.simplydo.dataSource

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.AudioPagingModel

internal val TAG_DS_AUDIO = DataSourceAudio::class.java.canonicalName

class DataSourceAudio(val context: Context) : PagingSource<Int, AudioModel>() {

    override fun getRefreshKey(state: PagingState<Int, AudioModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AudioModel> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = getAudio(nextPageNumber)
            Log.d(TAG_DS_AUDIO, "load: ${response.data.size}")

            LoadResult.Page(
                data = response.data,
                prevKey = null, // Only paging forward.
                nextKey = if (response.nextPage == -1) null else response.nextPage
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private fun getAudio(nextPageNumber: Int): AudioPagingModel {
        val audioArrayList = ArrayList<AudioModel>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION
        )

        // Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            // Cache column indices.

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationUnit = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)
                val duration = cursor.getInt(durationUnit)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.

                audioArrayList.add(AudioModel(contentUri.toString(), name, duration, size))
            }
            query.close()
        }

        return AudioPagingModel(nextPage = -1, data = audioArrayList)
    }
}
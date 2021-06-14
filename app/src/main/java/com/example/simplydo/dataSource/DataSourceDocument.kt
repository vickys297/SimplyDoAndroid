package com.example.simplydo.dataSource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.attachmentModel.DocumentDataSourceModel
import com.example.simplydo.model.attachmentModel.DocumentModel

internal val TAG_DS_DOCUMENT = DataSourceDocument::class.java.canonicalName

class DataSourceDocument(val context: Context) : PagingSource<Int, DocumentModel>() {
    override fun getRefreshKey(state: PagingState<Int, DocumentModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DocumentModel> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = getDocumentList(nextPageNumber)
            Log.d(TAG_DS_DOCUMENT, "load: getDocumentList ${response.data.size}")

            LoadResult.Page(
                data = response.data,
                prevKey = null, // Only paging forward.
                nextKey = if (response.nextPage == -1) null else response.nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    private fun getDocumentList(nextPageNumber: Int): DocumentDataSourceModel {

        val arrayListDocument = ArrayList<DocumentModel>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.VOLUME_NAME,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
        )
        val selection =
            "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
        val selectionArgs = null
        val sortOrder = null

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            projection,
            null,
            selectionArgs,
            sortOrder
        )

        cursor?.use {

            it.moveToFirst()
            Log.i(TAG_DS_DOCUMENT, "getDocumentList: count ${cursor.count}")
            while (it.moveToNext()) {

                val columnId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val fileSize = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val createdDate =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val fileType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)


                val id = cursor.getLong(columnId)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(fileSize)
                val createdAt = cursor.getString(createdDate)
                val fileDataType = cursor.getInt(fileType)
                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))


                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Files.getContentUri(MediaStore.Files.FileColumns.VOLUME_NAME),
                    id
                )

                Log.i(TAG_DS_DOCUMENT, "getDocumentList: $id/$name")

                val newData = DocumentModel(
                    id = id,
                    name = name,
                    size = size,
                    createdAt = createdAt,
                    fileDataType = fileDataType,
                    mimeType = mimeType,
                    contentUri = contentUri.toString()
                )
                arrayListDocument.add(newData)
            }

            it.close()
        }


        for (item in arrayListDocument) {
            Log.i(TAG_DS_DOCUMENT, "getDocumentList: ${item.name}/${item.fileDataType}")
        }

        return DocumentDataSourceModel(nextPage = -1, data = arrayListDocument)
    }
}
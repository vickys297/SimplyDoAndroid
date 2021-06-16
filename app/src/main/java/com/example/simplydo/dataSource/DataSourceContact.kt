package com.example.simplydo.dataSource

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.attachmentModel.ContactPagingModel


private val TAG_CONTACT_PAGING = DataSourceContactNew::class.java.canonicalName

class DataSourceContactNew(val context: Context) : PagingSource<Int, ContactModel>() {


    override fun getRefreshKey(state: PagingState<Int, ContactModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactModel> {
        return try {
            val response = getContactList()
            Log.d(TAG_CONTACT_PAGING, "load: ${response.data.size}")

            LoadResult.Page(
                data = response.data,
                prevKey = null, // Only paging forward.
                nextKey = if (response.nextPage == -1) null else response.nextPage
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    @WorkerThread
    private fun getContactList(): ContactPagingModel {
        val arrayContactModel = ArrayList<ContactModel>()

        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Contactables.DATA,
        )

        val selection = ContactsContract.Data.MIMETYPE + " in (?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        val sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY
        val uri: Uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI


        val cursor: Cursor? =
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.use {
            val idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
            val thumbnailIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
            val photoIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idIdx)
                val name = cursor.getString(nameIdx)
                val data = cursor.getString(dataIdx)

                val thumbnailUri = cursor.getString(thumbnailIdx)
                val photoUri = cursor.getString(photoIdx)

                arrayContactModel.add(
                    ContactModel(
                        id = id,
                        photoThumbnailUri = thumbnailUri,
                        photoUri = photoUri,
                        name = name,
                        mobile = data
                    )
                )

                Log.i(TAG_CONTACT_PAGING, "getContactList: $name/$data")
            }
            cursor.close()
        }

        return ContactPagingModel(nextPage = -1, data = arrayContactModel)
    }

}

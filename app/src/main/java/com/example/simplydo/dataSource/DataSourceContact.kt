package com.example.simplydo.dataSource

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.util.LongSparseArray
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
        val arrayList = ArrayList<ContactModel>()
        val arrayLongSparse = LongSparseArray<ContactModel>()

        val contentResolver = context.contentResolver
        val start = System.currentTimeMillis()

        val projection = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE
        )
        val selection = ContactsContract.Data.MIMETYPE + " in (?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        val sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY

        val uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.run {

            val mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)
            val idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
            val typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE)

            val thumbnailIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
            val photoIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIdx)
                val type = cursor.getInt(typeIdx)
                val name = cursor.getString(nameIdx)
                val data = cursor.getString(dataIdx)
                val mimeType = cursor.getString(mimeTypeIdx)

                val thumbnailUri = cursor.getString(thumbnailIdx)
                val photoUri = cursor.getString(photoIdx)

                var contactModel: ContactModel? = arrayLongSparse.get(id)
                if (contactModel == null) {
                    contactModel = ContactModel(
                        id = id, name = name,  photoUri = photoUri, photoThumbnailUri = thumbnailUri
                    )
                    arrayLongSparse.put(id, contactModel)
                    arrayList.add(contactModel)
                }


                if (mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) {
                    if (!contactModel.email.contains(data))
                        contactModel.email.add(data)
                } else {
                    if (!contactModel.mobile.contains(data))
                        contactModel.mobile.add(data)
                }
            }


            val ms = System.currentTimeMillis() - start


            arrayList.forEach {
                Log.i(TAG, "getContactList: ${it.name}/${it.mobile}/${it.email}")
            }
            Log.i(TAG, "getContactList: $ms")
            cursor.close()
        }

        return ContactPagingModel(nextPage = -1, data = arrayList)
    }

}

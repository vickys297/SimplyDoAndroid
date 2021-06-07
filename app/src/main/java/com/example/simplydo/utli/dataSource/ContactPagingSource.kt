package com.example.simplydo.utli.dataSource

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.attachmentModel.ContactPagingModel

internal val TAG = ContactPagingSource::class.java.canonicalName

class ContactPagingSource(val context: Context) : PagingSource<Int, ContactModel>() {
    override fun getRefreshKey(state: PagingState<Int, ContactModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactModel> {

        try {
            val nextPageNumber = params.key ?: 0
            val response = getContactList(nextPageNumber)
            Log.i(TAG, "load: ${response.contacts.size}")

            return LoadResult.Page(
                data = response.contacts,
                prevKey = null, // Only paging forward.
                nextKey = if (response.nextPage == -1) null else response.nextPage
            )

        } catch (e: Exception) {
            throw e
        }
    }

    private fun getContactList(nextPageNumber: Int): ContactPagingModel {
        val pageSize = 30
        val contactModel = ArrayList<ContactModel>()
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC LIMIT $pageSize OFFSET $nextPageNumber"
        )

        cursor?.let {

            while (cursor.moveToNext()) {
                val id = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                    )
                )

                val thumbnailUri =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                val photoUri =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                val contactName = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )

                val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                val selection =
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} =?"


                val phoneCursor = context.contentResolver.query(
                    uriPhone,
                    null,
                    selection,
                    arrayOf<String>(id),
                    null
                )

                phoneCursor?.let {
                    while (phoneCursor.moveToNext()) {

                        val phoneNumber = phoneCursor.getString(
                            phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )

                        val model = ContactModel(
                            photoThumbnailUri = thumbnailUri,
                            photoUri = photoUri,
                            name = contactName,
                            mobile = phoneNumber
                        )
                        contactModel.add(model)
                    }
                    phoneCursor.close()
                }
            }

        }
        cursor?.close()


        val remainingData = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC LIMIT $pageSize OFFSET ${nextPageNumber + pageSize}"
        )

        var incrementer = -1

        remainingData?.let {
            incrementer = if (remainingData.count == pageSize) nextPageNumber + pageSize else -1
            remainingData.close()
        }

        return ContactPagingModel(nextPage = incrementer, contactModel)
    }

}
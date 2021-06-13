package com.example.simplydo.utli.dataSource

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.attachmentModel.ContactPagingModel
import java.util.*

private val TAG_CONTACT_PAGING = ContactPagingSource::class.java.canonicalName

class ContactPagingSource(val context: Context) : PagingSource<Int, ContactModel>() {
    override fun getRefreshKey(state: PagingState<Int, ContactModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactModel> {

        return   try {
            val nextPageNumber = params.key ?: 0
            val response = getContactList(nextPageNumber)
            Log.d(TAG_CONTACT_PAGING, "load: ${response.contacts.size}")

             LoadResult.Page(
                data = response.contacts,
                prevKey = null, // Only paging forward.
                nextKey = if (response.nextPage == -1) null else response.nextPage
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private fun getContactList(nextPageNumber: Int): ContactPagingModel {
        Log.i(com.example.simplydo.utli.TAG, "getContactList: $nextPageNumber")
        val pageSize = 100
        val contactModel = ArrayList<ContactModel>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        val sortOrder =
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC LIMIT $pageSize, $nextPageNumber"




        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Log.i(TAG, "getContactList: 1")
            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI.buildUpon().encodedQuery("LIMIT $pageSize, $nextPageNumber").build(),
                projection,
                null,
                null,
                sortOrder
            )
            cursor?.let {

                Log.i(TAG_CONTACT_PAGING, "getContactList: cursor --> ${cursor.count}")

                while (cursor.moveToNext()) {

                    val id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    )

                    val selection =
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} LIKE ?"

                    val phoneProjection = arrayOf(
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                        ContactsContract.Contacts.PHOTO_URI,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )

                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        selection,
                        arrayOf<String>(id),
                        null
                    )

                    phoneCursor?.let {
                        while (phoneCursor.moveToNext()) {

                            val thumbnailUri =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                                    )
                                )

                            val photoUri =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.PHOTO_URI
                                    )
                                )

                            val contactName = phoneCursor.getString(
                                phoneCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts.DISPLAY_NAME
                                )
                            )

                            val phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndexOrThrow(
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
        } else {
            Log.i(TAG, "getContactList: 2")
            val bundle = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, 100)
                putInt(ContentResolver.QUERY_ARG_OFFSET, nextPageNumber)
                putString(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
                )
            }


            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection, bundle, null
            )
            cursor?.let {

                Log.i(TAG_CONTACT_PAGING, "getContactList: cursor --> ${cursor.count}")

                while (cursor.moveToNext()) {

                    val id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    )

                    val selection =
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} LIKE ?"

                    val phoneProjection = arrayOf(
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                        ContactsContract.Contacts.PHOTO_URI,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )

                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        selection,
                        arrayOf<String>(id),
                        null
                    )

                    phoneCursor?.let {
                        while (phoneCursor.moveToNext()) {

                            val thumbnailUri =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                                    )
                                )

                            val photoUri =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.PHOTO_URI
                                    )
                                )

                            val contactName = phoneCursor.getString(
                                phoneCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts.DISPLAY_NAME
                                )
                            )

                            val phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndexOrThrow(
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
        }


        val remainingData = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC LIMIT $pageSize OFFSET ${nextPageNumber + pageSize}"
        )

        val incrementer = if (remainingData?.count != 0) {
            nextPageNumber + pageSize
        } else {
            -1
        }

        remainingData?.close()

        return ContactPagingModel(nextPage = incrementer, contactModel)
    }
}

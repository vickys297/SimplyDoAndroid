package com.example.simplydo.workerClass

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.AppConstant


internal val TAG = ContactListWorker::class.java.canonicalName


private val FROM_COLUMNS: Array<String> = arrayOf(
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)

class ContactListWorker(applicationContext: Context, workerParams: WorkerParameters) :
    Worker(applicationContext, workerParams) {

    override fun doWork(): Result {
        return try {
            val response = getContactsList()
            val outputData: Data = workDataOf(AppConstant.KEY_RESULT to response)
            Result.success(outputData)
        } catch (e: Exception) {
            Log.i(TAG, "doWork: Exception-> $e")
            Result.failure()
        }
    }

    private fun getContactsList(): ArrayList<ContactModel> {

        val contactModelArray = ArrayList<ContactModel>()

        val cursor = applicationContext.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )

        cursor?.let {

            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID
                ))

                val photoThumbnailUri = cursor.getBlob(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                ))
                val photoUri = cursor.getBlob(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_URI
                ))

                val contactName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME
                ))

                val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                val selection =
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} LIKE ?"


                val phoneCursor = applicationContext.contentResolver.query(
                    uriPhone,
                    null,
                    selection,
                    arrayOf<String>(id),
                    null
                )

                phoneCursor?.let {
                    while (phoneCursor.moveToNext()) {

                        val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ))

                        val model = ContactModel(
                            photoThumbnailUri = photoThumbnailUri,
                            photoUri = photoUri,
                            name = contactName,
                            mobile = phoneNumber
                        )
                        contactModelArray.add(model)
                    }
                    phoneCursor.close()
                }

                contactModelArray.forEach {
                    Log.i(TAG, "getContactsList: ${it.name}/${it.mobile}")
                }

            }

        }
        cursor?.close()



        return contactModelArray
    }


}
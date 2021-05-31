package com.example.simplydo.ui.fragments.gallaryListView

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

internal val TAG = GalleryListViewModel::class.java.canonicalName
class GalleryListViewModel(context: Context) : ViewModel() {
    // TODO: Implement the ViewModel


    fun getGalleryItemCount(requireActivity: FragmentActivity) {

// Get relevant columns for use later.
        // Get relevant columns for use later.
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
        )

        // Return only video and image metadata.
        // Return only video and image metadata.
        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val queryUri: Uri = MediaStore.Files.getContentUri("external")

//        val cursorLoader = CursorLoader(
//            context,
//            queryUri,
//            projection,
//            selection,
//            null,  // Selection args (none).
//            MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
//        )
//        val cursor: Cursor = cursorLoader.loadInBackground()

        val cursor = requireActivity.contentResolver.query(
            queryUri,
            projection,
            selection,
            null,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 50"
        )

        cursor?.let {

            Log.i(TAG, "getGalleryList: ${cursor.count}")
            cursor.close()
        }


    }


}
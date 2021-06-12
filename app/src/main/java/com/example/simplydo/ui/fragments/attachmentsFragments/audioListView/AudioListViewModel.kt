package com.example.simplydo.ui.fragments.attachmentsFragments.audioListView

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplydo.model.attachmentModel.AudioModel

class AudioListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val audioList = MutableLiveData<ArrayList<AudioModel>>()

    @SuppressLint("InlinedApi")
    fun getAudioList(requireActivity: FragmentActivity) {
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

        val query = requireActivity.contentResolver.query(
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
            audioList.postValue(audioArrayList)
            query.close()
        }
    }
}
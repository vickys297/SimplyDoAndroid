package com.example.simplydo.model.attachmentModel

import android.net.Uri
import com.example.simplydo.model.ContactModel


data class ContactPagingModel(
    val nextPage: Int,
    val contacts: ArrayList<ContactModel>,
)


data class TodoAttachmentModel(
    val documentsList: ArrayList<String>,
    val audioList: ArrayList<AudioModel>,
    val imagesList: ArrayList<String>,
    val contactArray: ArrayList<ContactModel>,
    val location: String,
)

data class AudioModel(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
)

data class GalleryModel(
    val id: Long,
    val name: String,
    val size: Int,
    val createdAt: String,
    val fileDataType: Int,
    val mimeType: String,
    val contentUri: Uri
)

data class GalleryPagingModel(
    val nextPage: Int,
    val data: ArrayList<GalleryModel>,
)
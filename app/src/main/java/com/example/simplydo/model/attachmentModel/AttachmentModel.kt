package com.example.simplydo.model.attachmentModel

import com.example.simplydo.model.ContactModel


data class ContactPagingModel(
    val nextPage: Int,
    val contacts: ArrayList<ContactModel>,
)


data class AttachmentModel(
    val documentsList: ArrayList<String>,
    val audioList: ArrayList<AudioModel>,
    val imagesList: ArrayList<String>,
    val contactArray: ArrayList<ContactModel>,
    val location: String,
)

data class AudioModel(
    val uri: String,
    val name: String,
    val duration: Int,
    val size: Int,
    var isSelected: Boolean = false
)

data class GalleryModel(
    val id: Long,
    val name: String,
    val size: Int,
    val createdAt: String,
    val fileDataType: Int,
    val mimeType: String,
    val contentUri: String,
    var isSelected: Boolean = false
)

data class DocumentModel(
    val id: Long,
    val name: String,
    val size: Int,
    val createdAt: String,
    val fileDataType: Int,
    val mimeType: String,
    val contentUri: String,
    var isSelected: Boolean = false
)

data class GalleryPagingModel(
    val nextPage: Int,
    val data: ArrayList<GalleryModel>,
)
package com.example.simplydo.model.attachmentModel

import com.example.simplydo.model.ContactModel
import java.io.Serializable

data class AudioPagingModel(
    val nextPage: Int,
    val data: ArrayList<AudioModel>
)

data class ContactPagingModel(
    val nextPage: Int,
    val data: ArrayList<ContactModel>
)

data class GalleryPagingModel(
    val nextPage: Int,
    val data: ArrayList<GalleryModel>
)


data class AttachmentModel(
    val documentsList: ArrayList<FileModel>,
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
) : Serializable

data class GalleryModel(
    val id: Long,
    val name: String,
    val size: Int,
    val createdAt: String,
    val fileDataType: Int,
    val mimeType: String,
    val contentUri: String,
    var isSelected: Boolean = false
) : Serializable

data class FileModel(
    val id: Long,
    val name: String,
    val size: Int,
    val createdAt: String?,
    val fileDataType: Int,
    val mimeType: String,
    val contentUri: String,
    var isSelected: Boolean = false
) : Serializable


data class CalenderDateSelectorModel(
    var startEventDate: Long,
    var endEventDate: Long,
)
package com.example.simplydo.model.attachmentModel

import com.example.simplydo.model.ContactModel


data class AttachmentModel(
    val contactArray: ArrayList<ContactModel>,
    val location: String,
)

data class ContactPagingModel(
    val nextPage: Int,
    val contacts: ArrayList<ContactModel>,
)
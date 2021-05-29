package com.example.simplydo.model.attachmentModel

data class ContactModel(
    val photoThumbnailUri: ByteArray?,
    val photoUri: ByteArray?,
    val name: String,
    val mobile: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactModel

        if (!photoThumbnailUri.contentEquals(other.photoThumbnailUri)) return false
        if (!photoUri.contentEquals(other.photoUri)) return false
        if (name != other.name) return false
        if (mobile != other.mobile) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoThumbnailUri.contentHashCode()
        result = 31 * result + photoUri.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mobile.hashCode()
        return result
    }
}


data class AttachmentModel(
    val contactArray: ArrayList<ContactModel>,
    val location: String,
)

data class ContactPagingModel(
    val nextPage: Int,
    val contacts: ArrayList<ContactModel>,
)
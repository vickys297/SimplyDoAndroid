package com.example.simplydo.model

import java.io.Serializable

data class UserModel(
    val profilePicture: String = "https://picsum.photos/200",
    val firstName: String,
    var middleName: String? = null,
    val lastName: String,
    val email: String,
    val phone: String,
    val uKey: String,
):Serializable {
    fun getUserName(): String {
        return String.format(
            "%s %s", firstName, if (!middleName.isNullOrEmpty()) {
                "$middleName $lastName"
            } else {
                lastName
            }
        )
    }
}
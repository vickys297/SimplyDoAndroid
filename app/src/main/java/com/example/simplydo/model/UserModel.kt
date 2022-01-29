package com.example.simplydo.model

data class UserModel(
    val profilePicture: String = "https://picsum.photos/200",
    val firstName: String,
    var middleName: String = "",
    val lastName: String,
    val email: String,
    val phone: String,
    val uKey: String,
)
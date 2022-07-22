package com.example.simplydo.model

data class LoginModel(
    val phone: String,
)

data class LoginResponseModel(
    val result: String,
    val message: String,
    val data: LoginResponseDataModel
)

data class OTPModel(
    val phone: String,
    val otp: String
)

data class OTPResponse(
    val result: String,
    val message: String,
    val data: LoginResponseDataModel
)

data class LoginResponseDataModel(
    val isVerified: Boolean,
    val uKey: String,
    val phone: String
)
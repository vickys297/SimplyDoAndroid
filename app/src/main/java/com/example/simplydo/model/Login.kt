package com.example.simplydo.model

data class LoginModel(
    val mobile: String,
)

data class LoginResponseModel(
    val result: String,
    val message: String,
    val data: LoginResponseDataModel
)


data class OTPModel(
    val mobile: String,
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
    val mobile: String
)



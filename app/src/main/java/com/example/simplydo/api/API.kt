package com.example.simplydo.api

import com.example.simplydo.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface API {

    @POST("token")
    fun registerNewUser(@Body token: Token): Call<TokenResponse>

    @POST("login")
    fun loginUser(@Body loginModel: LoginModel): Call<LoginResponseModel>

    @POST("verify")
    fun validateOTP(@Body loginOTPModel: OTPModel): Call<OTPResponse>
}
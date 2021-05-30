package com.example.simplydo.api

import com.example.simplydo.model.*
import retrofit2.Call
import retrofit2.http.*

interface API {

    @POST("token")
    fun registerNewUser(@Body token: Token): Call<TokenResponse>

    @POST("login")
    fun loginUser(@Body loginModel: LoginModel): Call<LoginResponseModel>

    @POST("verify")
    fun validateOTP(@Body loginOTPModel: OTPModel): Call<OTPResponse>

    @POST("todo/{uKey}")
    fun newEntryTodo(
        @Body todoModel: TodoModel,
        @Path("uKey") uKey: String,
    ): Call<CommonResponseModel>

    @POST("todo/{uKey}/sync")
    fun uploadDataToCloudDatabase(
        @Body todoModels: ArrayList<TodoModel>,
        @Path("uKey") uKey: String,
    ): Call<CommonResponseModel>


    // get data from cloud by uKey and date
    @GET("todo/{uKey}")
    fun syncFromCloudByDate(
        @Path("uKey") uKey: String,
        @QueryMap dateString: HashMap<String, String>,
    ): Call<RequestDataFromCloudResponseModel>
}
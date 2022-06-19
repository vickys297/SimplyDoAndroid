package com.example.simplydo.api.network

import android.content.Context
import com.example.simplydo.utils.AppConstant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection


class RetrofitServices(context: Context) {

    private var retrofit: Retrofit.Builder

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeout = 30L, TimeUnit.SECONDS)
            .readTimeout(timeout = 30L, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor.getInstance(context))
            .addInterceptor(interceptor)
            .hostnameVerifier { _, session ->
                val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                hv.verify(AppConstant.Network.VERIFY_WEBSITE, session)
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.Network.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())

    }

    companion object {
        @Volatile
        var instance: RetrofitServices? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: RetrofitServices(context).also { instance = it }
            }
    }

    fun <S> createService(serviceClass: Class<S>): S {
        val builder = retrofit.build()
        return builder.create(serviceClass)
    }
}
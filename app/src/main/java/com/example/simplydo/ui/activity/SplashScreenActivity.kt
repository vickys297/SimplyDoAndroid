package com.example.simplydo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplydo.R
import com.example.simplydo.api.API
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.Token
import com.example.simplydo.model.TokenResponse
import com.example.simplydo.network.NoConnectivityException
import com.example.simplydo.network.RetrofitServices
import com.example.simplydo.ui.MainActivity
import com.example.simplydo.ui.fragments.login.LoginActivity
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppPreference
import com.example.simplydo.utli.AppRepository
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal val TAG: String = SplashScreenActivity::class.java.canonicalName.toString()

class SplashScreenActivity : AppCompatActivity() {

    lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        appRepository = AppRepository.getInstance(this@SplashScreenActivity,
            AppDatabase.getInstance(context = this@SplashScreenActivity))
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({

            val uuid = AppPreference.getPreferences(AppConstant.UUID, "", this@SplashScreenActivity)

            if (uuid.isNullOrEmpty() && uuid.isNullOrBlank()) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    Log.i(TAG, "New Token $token")
                    token?.let {
                        registerNewUser(it)
                    }
                }
            } else {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)

    }

    private fun registerNewUser(token: String) {
        println("registerNewUser $token")
        val client = RetrofitServices.getInstance(context = this).createService(API::class.java)
        val request = client.registerNewUser(Token(deviceToken = token))
        request.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                val data = response.body()

                println("ResponseData -> $data")
                data?.let {

                    when (it.result) {
                        AppConstant.API_RESULT_OK -> {
                            AppPreference.storePreferences(AppConstant.UUID, it.uuid, this@SplashScreenActivity)
                            Toast.makeText(this@SplashScreenActivity, it.message, Toast.LENGTH_LONG)
                                .show()
                            goToLoginActivity()
                        }
                        AppConstant.API_RESULT_ERROR -> {
                            Toast.makeText(this@SplashScreenActivity, it.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            }

            override fun onFailure(call: Call<TokenResponse?>?, t: Throwable?) {
                if (t is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    showNoNetworkMessage()
                }
            }

        })

    }

    private fun goToLoginActivity() {
        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showNoNetworkMessage() {
        Toast.makeText(this@SplashScreenActivity, "No network available", Toast.LENGTH_LONG).show()
    }
}
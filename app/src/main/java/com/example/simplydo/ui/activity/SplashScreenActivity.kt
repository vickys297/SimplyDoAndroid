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
import com.example.simplydo.api.network.NoConnectivityException
import com.example.simplydo.api.network.RetrofitServices
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.Token
import com.example.simplydo.model.TokenResponse
import com.example.simplydo.ui.activity.login.LoginActivity
import com.example.simplydo.ui.activity.personalWorkspace.PersonalWorkspaceActivity
import com.example.simplydo.ui.activity.privateWorkspace.WorkspaceActivity
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppPreference
import com.example.simplydo.utils.AppRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mobi.business.simplydoscheduler.SimplyDoScheduler
import com.mobi.business.simplydoscheduler.model.DateTimeModel
import com.mobi.business.simplydoscheduler.model.DayModel
import com.mobi.business.simplydoscheduler.model.NotificationDataModel
import com.mobi.business.simplydoscheduler.model.TimeModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

internal val TAG = SplashScreenActivity::class.java.canonicalName

class SplashScreenActivity : AppCompatActivity() {

    lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, "Token >> $token")
        })


        val scheduler = SimplyDoScheduler.getInstance(this@SplashScreenActivity)
        scheduler.scheduleRepeatedNotification(
            NotificationDataModel(
                title = "Title",
                message = "Message",
                dateTime = DateTimeModel(
                    date = DayModel(
                        year = 2020, month = 1, day = 1
                    ),
                    time = TimeModel(
                        hours = 11,
                        minutes = 36
                    ),
                ),
                id = 2313,
                setOf(
                    Calendar.MONDAY,
                    Calendar.TUESDAY,
                    Calendar.WEDNESDAY,
                    Calendar.THURSDAY,
                    Calendar.FRIDAY,
                    Calendar.SATURDAY,
                    Calendar.SUNDAY
                ),
                repeatableType = 23
            )
        )

        appRepository = AppRepository.getInstance(
            this@SplashScreenActivity,
            AppDatabase.getInstance(context = this@SplashScreenActivity)
        )

    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if (AppPreference.getPreferences(
                    AppConstant.IS_LOGGED_IN,
                    false,
                    this@SplashScreenActivity
                )
            ) {
                val currentWorkspace = AppPreference.getPreferences(
                    AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
                    AppConstant.Workspace.DEFAULT_PERSONAL_WORKSPACE,
                    this@SplashScreenActivity
                )
                if (currentWorkspace == AppConstant.Workspace.DEFAULT_PERSONAL_WORKSPACE) {
                    startActivity(
                        Intent(this@SplashScreenActivity, PersonalWorkspaceActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                } else {
                    startActivity(
                        Intent(this@SplashScreenActivity, WorkspaceActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                }
                finish()
            } else {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    Log.d(TAG, "New Token $token")
                    token?.let {
                        registerNewUser(it)
                    }
                }
            }
        }, 1000)
    }

    private fun registerNewUser(token: String) {
        println("registerNewUser $token")
        val client = RetrofitServices.getInstance(context = this).createService(API::class.java)
        val request = client.registerNewUser(Token(deviceToken = token))

        request.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                val data = response.body()
                data?.let {
                    when (it.result) {
                        AppConstant.API_RESULT_OK -> {
                            AppPreference.storePreferences(
                                AppConstant.UUID,
                                it.uuid,
                                this@SplashScreenActivity
                            )
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
                    // show No Connectivity message to account or do whatever you want.
                    showNoNetworkMessage()
                }
            }
        })
    }

    private fun goToLoginActivity() {
        startActivity(
            Intent(this@SplashScreenActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
        finish()
    }

    private fun showNoNetworkMessage() {
        Toast.makeText(this@SplashScreenActivity, "No network available", Toast.LENGTH_LONG).show()
    }
}
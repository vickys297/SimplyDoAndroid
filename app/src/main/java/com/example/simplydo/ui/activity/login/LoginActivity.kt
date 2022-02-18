package com.example.simplydo.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.simplydo.R
import com.example.simplydo.api.API
import com.example.simplydo.api.network.NoConnectivityException
import com.example.simplydo.api.network.RetrofitServices
import com.example.simplydo.databinding.ActivityLoginBinding
import com.example.simplydo.model.LoginModel
import com.example.simplydo.model.LoginResponseModel
import com.example.simplydo.model.OTPModel
import com.example.simplydo.model.OTPResponse
import com.example.simplydo.ui.activity.personalWorkspace.PersonalWorkspaceActivity
import com.example.simplydo.ui.activity.privateWorkspace.WorkspaceActivity
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)


        binding.btnLogin.setOnClickListener {
            val mobile = binding.etMobile.text.toString()
            loginUser(mobile)
        }

        binding.btnVerifyOTP.setOnClickListener {
            val otp = binding.etOTP.text.toString()
            val mobile = binding.etMobile.text.toString()
            verifyOTP(mobile, otp)
        }
    }

    private fun verifyOTP(mobile: String, otp: String) {
        if (otp.isNotEmpty() && mobile.isNotEmpty()) {
            validateOTP(otp, mobile)
        } else {
            binding.etOTP.error = "OTP required"
        }
    }

    private fun validateOTP(otp: String, mobile: String) {
        val retrofitServices =
            RetrofitServices.getInstance(this@LoginActivity).createService(API::class.java)
        val validateOTP = retrofitServices.validateOTP(OTPModel(mobile = mobile, otp = otp))

        validateOTP.enqueue(object : Callback<OTPResponse> {
            override fun onResponse(
                call: Call<OTPResponse>,
                response: Response<OTPResponse>
            ) {
                val data = response.body()
                data?.let {
                    when (it.result) {
                        AppConstant.API_RESULT_OK -> {

                            if (it.data.isVerified) {

                                AppPreference.storePreferences(
                                    AppConstant.IS_LOGGED_IN,
                                    true,
                                    this@LoginActivity
                                )

                                AppPreference.storePreferences(
                                    AppConstant.USER_KEY,
                                    it.data.uKey,
                                    this@LoginActivity
                                )

                                AppPreference.storePreferences(
                                    AppConstant.USER_MOBILE,
                                    it.data.mobile,
                                    this@LoginActivity
                                )

                                goToMainActivity()
                            } else {
                                showOTPField()
                            }
                        }
                        AppConstant.API_RESULT_ERROR -> {
                            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                if (t is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    showNoNetworkMessage()
                }
            }

        })
    }

    private fun goToMainActivity() {

        val currentActiveScreen = AppPreference.getPreferences(
            AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
            AppConstant.Session.PERSONAL_SCREEN,
            this@LoginActivity
        )

        when (currentActiveScreen) {
            AppConstant.Session.PERSONAL_SCREEN -> {
                val intent = Intent(this@LoginActivity, PersonalWorkspaceActivity::class.java)
                startActivity(intent)
                finish()
            }
            AppConstant.Session.WORKSPACE_SCREEN -> {
                val intent = Intent(this@LoginActivity, WorkspaceActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                val intent = Intent(this@LoginActivity, PersonalWorkspaceActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun loginUser(mobile: String) {
        if (mobile.isNotEmpty()) {
            validateUser(mobile)
        } else {
            binding.etMobile.error = "Mobile Number required"
        }
    }

    private fun validateUser(mobile: String) {
        val retrofitServices =
            RetrofitServices.getInstance(this@LoginActivity).createService(API::class.java)
        val validateUser = retrofitServices.loginUser(LoginModel(mobile = mobile))
        validateUser.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                val data = response.body()
                data?.let {
                    when (it.result) {
                        AppConstant.API_RESULT_OK -> {


                            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()

                            if (!it.data.isVerified) {
                                showOTPField()
                            }
                        }
                        AppConstant.API_RESULT_ERROR -> {
                            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                if (t is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    showNoNetworkMessage()
                }
            }

        })
    }

    private fun showNoNetworkMessage() {
        Toast.makeText(this@LoginActivity, "No network available", Toast.LENGTH_LONG).show()
    }

    private fun showOTPField() {
        binding.textInputLayoutMobile.visibility = View.GONE
        binding.textInputLayoutOTP.visibility = View.VISIBLE

        binding.btnLogin.visibility = View.GONE
        binding.btnVerifyOTP.visibility = View.VISIBLE
    }
}
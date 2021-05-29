package com.example.simplydo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.ui.activity.SplashScreenActivity
import com.example.simplydo.ui.fragments.login.LoginActivity
import com.example.simplydo.utli.AppPreference
import com.example.simplydo.utli.AppConstant

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val userKey = AppPreference.getPreferences(AppConstant.USER_KEY, "", context = this@MainActivity)
        val uuid = AppPreference.getPreferences(AppConstant.UUID, "", this@MainActivity)

        if (uuid.isEmpty()) {
            val intent = Intent(this@MainActivity, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (userKey.isEmpty()) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onResume() {
        super.onResume()


    }
}
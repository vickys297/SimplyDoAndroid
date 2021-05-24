package com.example.simplydo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.screens.login.LoginActivity
import com.example.simplydo.screens.todoList.ToDoFragment
import com.example.simplydo.utli.Constant
import com.example.simplydo.utli.Session

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val userKey = Session.getSession(Constant.USER_KEY, "", context = this@MainActivity)
        val uuid = Session.getSession(Constant.UUID, "", this@MainActivity)

        if (uuid.isNullOrEmpty()) {
            val intent = Intent(this@MainActivity, SplashScreenActivity::class.java)
            startActivity(intent)
        }
        if (userKey.isNullOrEmpty()) {
            //
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onResume() {
        super.onResume()


    }
}
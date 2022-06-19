package com.example.simplydo.ui.activity.privateWorkspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.databinding.ActivityWorkspaceBinding

class WorkspaceActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var binding: ActivityWorkspaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@WorkspaceActivity, R.layout.activity_workspace)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


    }
}
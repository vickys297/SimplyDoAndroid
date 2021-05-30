package com.example.simplydo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.databinding.MainActivityBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.ui.activity.SplashScreenActivity
import com.example.simplydo.ui.fragments.login.LoginActivity
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppPreference
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = MainActivity::class.java.canonicalName

class MainActivity : AppCompatActivity() {


    private lateinit var allTodoListDataObserver: Observer<List<TodoModel>>
    private lateinit var totalTaskCountObserver: Observer<Int>
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.main_activity)

        setUpObserver()
        setUpViewModel()

        val userKey =
            AppPreference.getPreferences(AppConstant.USER_KEY, "", context = this@MainActivity)
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

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this@MainActivity, ViewModelFactory(this@MainActivity,
            AppRepository.getInstance(this@MainActivity,
                AppDatabase.getInstance(this@MainActivity)))).get(MainViewModel::class.java)
        binding.apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }

        viewModel.getAllTodoListNotSynced().observe(this, allTodoListDataObserver)
        viewModel.getTotalTaskCount().observe(this, totalTaskCountObserver)
    }

    private fun setUpObserver() {
        allTodoListDataObserver = Observer {
            if (it.isNotEmpty()) {
                (it as ArrayList<TodoModel>).forEach { it ->
                    Log.i(TAG, "Un Synced data :\n" +
                            "dtId: ${it.dtId}\n" +
                            "Title: ${it.title}\n" +
                            "Task: ${it.todo}\n" +
                            "Event Date: ${it.eventDate}\n" +
                            "Event Time: ${it.eventTime}")
                }
                viewModel.syncDataWithCloud(it)
            }
        }

        totalTaskCountObserver = Observer {
            if (it.equals(0)) {
                addDummyDataToLocalDataBase()
            }
        }

    }

    private fun addDummyDataToLocalDataBase() {

        val contactListStub: ArrayList<ContactModel> = ArrayList()
        val imagesListStub: ArrayList<String> = ArrayList()


        for (i in 0..100) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, i)

            viewModel.insertDummyDataIntoLocalDatabase(
                task = "task Title $i",
                title = "Sample dummy stub task $i",
                eventDate = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON)
                    .format(calendar.time),
                priority = i % 2 == 0,
                contactList = contactListStub,
                imageList = imagesListStub
            )
        }
    }
}
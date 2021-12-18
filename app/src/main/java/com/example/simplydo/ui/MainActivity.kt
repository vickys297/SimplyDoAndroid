package com.example.simplydo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.databinding.MainActivityBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.*
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = MainActivity::class.java.canonicalName

class MainActivity : AppCompatActivity() {


    private lateinit var allTodoListDataObserver: Observer<List<TodoModel>>

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.main_activity)

        setUpObserver()
        setUpViewModel()
        /*Default
        * Load all default values
        * */
        loadDefaults()

//        val userKey =
//            AppPreference.getPreferences(AppConstant.USER_KEY, "", context = this@MainActivity)
//        val uuid = AppPreference.getPreferences(AppConstant.UUID, "", this@MainActivity)
//
//        if (uuid.isEmpty()) {
//            val intent = Intent(this@MainActivity, SplashScreenActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        if (userKey.isEmpty()) {
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

//        intent.extras.let {
//            Log.i(TAG, "onCreate: ${it?.getLong(AppConstant.NAVIGATION_TASK_KEY)}")
//            Log.i(TAG, "onCreate: mainActivity bundle ${it.getLong(AppConstant.NAVIGATION_TASK_KEY, 0L)}")
//            if (it.getLong(AppConstant.NAVIGATION_TASK_KEY, 0L) != 0L) {
//                navController.navigate(R.id.todoFullDetailsFragment, intent.extras)
//            }
//        }

//        intent.getLongExtra(AppConstant.NAVIGATION_TASK_KEY, 0L).let {
//            Log.i(TAG, "onCreate: $it")
//            if (it != 0L) {
//                val bundle = Bundle()
//                bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, it)
//                navController.navigate(R.id.todoFullDetailsFragment, bundle)
//            }
//        }
    }

    private fun loadDefaults() {
        loadDefaultTags()
    }

    private fun loadDefaultTags() {
        val array = arrayOf("Work", "Personal", "Meetings", "Private", "Events")
        val arrayListTag = viewModel.getAvailableTagList()
        if (arrayListTag.isEmpty()) {
            for (tag in array) {
                viewModel.insertTag(tag)
            }
        }
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this@MainActivity, ViewModelFactory(
                this@MainActivity,
                AppRepository.getInstance(
                    this@MainActivity,
                    AppDatabase.getInstance(this@MainActivity)
                )
            )
        ).get(MainViewModel::class.java)
        binding.apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }

        viewModel.getAllTodoListNotSynced().observe(this, allTodoListDataObserver)

        if (!AppPreference.getPreferences(
                getString(R.string.preload_sample_data),
                false,
                this@MainActivity
            )
        ) {
            preLoadDummyData()
        }
    }

    private fun setUpObserver() {
        allTodoListDataObserver = Observer { tasks ->
//            if (tasks.isNotEmpty()) {
//                (tasks as ArrayList<TodoModel>).forEach {
//                    Log.d(
//                        TAG, "Un Synced data :\n" +
//                                "dtId: ${it.dtId}\n" +
//                                "Title: ${it.title}\n" +
//                                "Task: ${it.todo}\n" +
//                                "Event Date: ${it.eventDateTime}\n" +
//                                "Event Time: ${it.eventTime}"
//                    )
//                }
//                viewModel.syncDataWithCloud(tasks)
//            }
        }


    }


    private fun preLoadDummyData() {

        val calender = AppFunctions.getCurrentDateCalender()
        calender.set(Calendar.HOUR_OF_DAY, 23)
        calender.set(Calendar.MINUTE, 59)
        calender.set(Calendar.SECOND, 59)
        calender.set(Calendar.MILLISECOND, 0)


        viewModel.insertDummyDataIntoLocalDatabase(
            title = "Hi, your task title goes here",
            task = "Your task description here.\n" +
                    "Just swipe left or right to complete the task.",
            eventDate = calender.timeInMillis,
            taskPriority = 3,
            contactList = ArrayList(),
            imageList = ArrayList(),
            taskType = AppConstant.Task.TASK_TYPE_BASIC
        )
        viewModel.insertDummyDataIntoLocalDatabase(
            title = "Task Completed",
            task = "Your completed task looks like this.\n" +
                    "Completed task will be hidden if the date expires.\n" +
                    "You can access completed task by clicking options button.",
            eventDate = calender.timeInMillis,
            taskPriority = 3,
            isCompleted = true,
            contactList = ArrayList(),
            imageList = ArrayList(),
            taskType = AppConstant.Task.TASK_TYPE_BASIC
        )

        viewModel.insertDummyDataIntoLocalDatabase(
            title = "Basic task with priority",
            task = "This is the basic priority task, you will be notified by every morning before you start the day.\n" +
                    "You can change the notification time of the basic task.\n" +
                    "Goto Settings -> Notifications -> Basic Notification Time.",
            eventDate = calender.timeInMillis,
            taskPriority = 3,
            contactList = ArrayList(),
            imageList = ArrayList(),
            taskType = AppConstant.Task.TASK_TYPE_BASIC
        )

        calender.add(Calendar.DAY_OF_MONTH, 1)
        calender.set(Calendar.HOUR_OF_DAY, 10)
        calender.set(Calendar.MINUTE, 10)
        calender.set(Calendar.SECOND, 0)
        calender.set(Calendar.MILLISECOND, 0)

        viewModel.insertDummyDataIntoLocalDatabase(
            title = "Task with priority",
            task = "This is the priority task, you will be notified before 15 Minutes of the event time.\n" +
                    "Also you will be notified at the event time too.\n" +
                    "You can change the notification time of the basic task.\n" +
                    "Goto Settings -> Notifications -> Basic Notification Time.",
            eventDate = calender.timeInMillis,
            taskPriority = 3,
            contactList = ArrayList(),
            imageList = ArrayList(),
            taskType = AppConstant.Task.TASK_TYPE_EVENT
        )

        AppPreference.storePreferences(
            getString(R.string.preload_sample_data),
            true,
            this@MainActivity
        )
    }


}
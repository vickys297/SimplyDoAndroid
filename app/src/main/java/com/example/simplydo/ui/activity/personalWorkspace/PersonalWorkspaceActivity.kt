package com.example.simplydo.ui.activity.personalWorkspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.databinding.PersonalWorkspaceActivityBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.*
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.*
import com.google.gson.Gson
import java.util.*

internal val TAG = PersonalWorkspaceActivity::class.java.canonicalName

class PersonalWorkspaceActivity : AppCompatActivity() {


    private lateinit var allTodoListDataObserver: Observer<List<TodoModel>>

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var viewModel: PersonalWorkspaceViewModel
    private lateinit var binding: PersonalWorkspaceActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this@PersonalWorkspaceActivity,
                R.layout.personal_workspace_activity
            )

        setUpObserver()
        setUpViewModel()
        /*Default
        * Load all default values
        * */
        loadDefaults()

//        val userKey =
//            AppPreference.getPreferences(AppConstant.USER_KEY, "", context = this@PersonalWorkspaceActivity)
//        val uuid = AppPreference.getPreferences(AppConstant.UUID, "", this@PersonalWorkspaceActivity)
//
//        if (uuid.isEmpty()) {
//            val intent = Intent(this@PersonalWorkspaceActivity, SplashScreenActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        if (userKey.isEmpty()) {
//            val intent = Intent(this@PersonalWorkspaceActivity, LoginActivity::class.java)
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
            this@PersonalWorkspaceActivity, ViewModelFactory(
                this@PersonalWorkspaceActivity,
                AppRepository.getInstance(
                    this@PersonalWorkspaceActivity,
                    AppDatabase.getInstance(this@PersonalWorkspaceActivity)
                )
            )
        )[PersonalWorkspaceViewModel::class.java]
        binding.apply {
            viewModel = this@PersonalWorkspaceActivity.viewModel
            lifecycleOwner = this@PersonalWorkspaceActivity
            executePendingBindings()
        }

        viewModel.getAllTodoListNotSynced().observe(this, allTodoListDataObserver)

        if (!AppPreference.getPreferences(
                getString(R.string.preload_sample_data),
                false,
                this@PersonalWorkspaceActivity
            )
        ) {
            preLoadDummyData()
            setAsPaidUser()
            createPersonalWorkspace()
            createPrivateWorkspace()
            loadPaidPlan()
            createWorkspaceGroup()
        }
    }

    private fun createWorkspaceGroup() {
        val dataset = ArrayList<WorkspaceGroupModel>()

        for (item in 1..4) {
            dataset.add(
                WorkspaceGroupModel(
                    name = "Sample Group Name",
                    description = "Test Description",
                    workspaceID = item.toLong(),
                    createdBy = UserIdModel(
                        admin = Gson().fromJson(
                            AppPreference.getPreferences(
                                AppConstant.Preferences.USER_DATA,
                                this@PersonalWorkspaceActivity
                            ), UserModel::class.java
                        )
                    ),
                    people = arrayListOf(
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Vignesh",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "vignesh297@gmail.com",
                                phone = "8012215105",
                                uKey = "78325648771762178364"
                            ),
                            role = arrayListOf()
                        ),
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Sandeep",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "sandeep0312@gmail.com",
                                phone = "8012215105",
                                uKey = "783256487717621798664"
                            ),
                            role = arrayListOf()
                        ), UserAccountModel(
                            user = UserModel(
                                firstName = "Vignesh",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "vignesh297@gmail.com",
                                phone = "8012215105",
                                uKey = "78325648771762178364"
                            ),
                            role = arrayListOf()
                        ),
                        UserAccountModel(
                            user = UserModel(
                                firstName = "Sandeep",
                                middleName = "",
                                lastName = "Selvam",
                                profilePicture = "https://picsum.photos/200",
                                email = "sandeep0312@gmail.com",
                                phone = "8012215105",
                                uKey = "783256487717621798664"
                            ),
                            role = arrayListOf()
                        )
                    )
                )
            )
        }
        viewModel.createWorkspaceGroup(dataset)
    }

    private fun loadPaidPlan() {
        val plans = ArrayList<PaidPlanModel>()
        plans.add(
            PaidPlanModel(
                id = "1234",
                period = "Yearly",
                price = "169.00",
                textDescription = "Recommended by most of users",
                trailPeriodText = "30 days Free Trail",
                trialPeriod = 30,
                priority = true,
                currency = Currency.getInstance(Locale.getDefault()),
                selected = true
            )
        )
        plans.add(
            PaidPlanModel(
                id = "2345",
                period = "Monthly",
                price = "24.00",
                textDescription = "Recommended by most of small and medium",
                trailPeriodText = "7 days Free Trail",
                trialPeriod = 7,
                priority = true,
                currency = Currency.getInstance(Locale.getDefault()),
                selected = false
            )
        )

        AppPreference.storePreferences(
            AppConstant.Preferences.PAID_PLANS,
            Gson().toJson(plans),
            this@PersonalWorkspaceActivity
        )
    }


    private fun createPrivateWorkspace() {
        AppPreference.storePreferences(
            AppConstant.Preferences.ORGANIZATION_ID,
            8927576298374326462L,
            this@PersonalWorkspaceActivity
        )
        val userModel = UserModel(
            firstName = "Vignesh",
            middleName = "",
            lastName = "Selvam",
            email = "vignesh297@gmail.com",
            phone = AppPreference.getPreferences(
                AppConstant.USER_MOBILE,
                this@PersonalWorkspaceActivity
            ),
            uKey = AppPreference.getPreferences(
                AppConstant.USER_KEY,
                this@PersonalWorkspaceActivity
            )
        )

        AppPreference.storePreferences(
            AppConstant.Preferences.USER_DATA,
            Gson().toJson(userModel),
            this@PersonalWorkspaceActivity
        )
        val personalWorkspace = WorkspaceModel(
            wId = -1,
            orgId = -1,
            accountType = "",
            title = "Personal Workspace",
            moreDetails = "",
            createdBy = UserIdModel(userModel),
            users = arrayListOf()
        )

        AppPreference.storePreferences(
            AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
            personalWorkspace.wId,
            this@PersonalWorkspaceActivity
        )

        val workspace = WorkspaceModel(
            wId = -1,
            orgId = 927813698371976L,
            accountType = "",
            title = "Simply Do",
            moreDetails = "",
            createdBy = UserIdModel(userModel),
            users = arrayListOf(
                UserModel(
                    firstName = "Vignesh",
                    lastName = "Selvam",
                    email = "vignesh297@gmail.com",
                    phone = "8012215105",
                    uKey = "1000001"
                ),
                UserModel(
                    firstName = "Sandeep",
                    lastName = "Selvam",
                    email = "sandeep@test.com",
                    phone = "9876543210",
                    uKey = "1000002"
                ),
                UserModel(
                    firstName = "Ram",
                    lastName = "",
                    email = "ram@gmail.com",
                    phone = "9876543211",
                    uKey = "1000003"
                ),
                UserModel(
                    firstName = "Joe",
                    lastName = "",
                    email = "joe@gmail.com",
                    phone = "9876543212",
                    uKey = "1000004"
                )
            )
        )

        viewModel.storePrivateSpace(personalWorkspace)
        viewModel.storePrivateSpace(workspace)
    }

    private fun setAsPaidUser() {
        AppPreference.storePreferences(
            AppConstant.Preferences.IS_PAID_USER,
            true,
            this@PersonalWorkspaceActivity
        )
    }

    private fun createPersonalWorkspace() {
        val defaultPersonalWorkspace = PersonalWorkspaceModel(
            admin = UserModel(
                firstName = "Vignesh",
                middleName = "",
                lastName = "Selvam",
                email = "vignesh297@gmail.com",
                phone = AppPreference.getPreferences(
                    AppConstant.USER_MOBILE,
                    this@PersonalWorkspaceActivity
                ),
                uKey = AppPreference.getPreferences(
                    AppConstant.USER_KEY,
                    this@PersonalWorkspaceActivity
                )
            )
        )
        val datSet = Gson().toJson(defaultPersonalWorkspace)
        AppPreference.storePreferences(
            AppConstant.Preferences.PERSONAL_WORKSPACE, datSet, this@PersonalWorkspaceActivity
        )
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
            this@PersonalWorkspaceActivity
        )
    }


}
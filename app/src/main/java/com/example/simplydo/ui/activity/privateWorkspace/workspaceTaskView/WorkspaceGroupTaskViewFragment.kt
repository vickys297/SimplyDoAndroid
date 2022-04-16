package com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskView

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.workspace.WorkspaceGroupTaskViewAdapter
import com.example.simplydo.components.PieChartAnimation
import com.example.simplydo.databinding.WorkspaceGroupTaskFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.workspaceDialog.WorkspaceTaskBottomSheetDialog
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.GroupTaskByProgressModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.*
import java.util.*

internal val TAG = WorkspaceGroupTaskViewFragment::class.java.canonicalName
class WorkspaceGroupTaskViewFragment : Fragment(R.layout.workspace_group_task_fragment) {

    companion object {
        fun newInstance() = WorkspaceGroupTaskViewFragment()
    }

    private lateinit var workspaceGroupTaskArrayList: ArrayList<WorkspaceGroupTaskModel>
    private lateinit var observerWorkspaceGroupTaskArrayList: Observer<List<WorkspaceGroupTaskModel>>

    /*
    * Filter By
    * 0 - Status default
    * 1 - Date
    * 2 - Priority
    * */
    private var filterTaskBy = 0

    private lateinit var viewModel: WorkspaceGroupTaskViewModel
    private lateinit var binding: WorkspaceGroupTaskFragmentBinding
    private lateinit var groupData: WorkspaceGroupModel
    private lateinit var groupTaskByProgressModel: ArrayList<GroupTaskByProgressModel>
    private lateinit var workspaceGroupTaskViewAdapter: WorkspaceGroupTaskViewAdapter

    private var orderBy = 1

    private val filterCallback = object : WorkspaceTaskBottomSheetDialog.Callback {
        override fun filterByDate(orderBy: Int) {
            this@WorkspaceGroupTaskViewFragment.orderBy = orderBy
            filterTaskBy = 1
            getTaskByProgressStatus(workspaceGroupTaskArrayList)
        }

        override fun filterByPriority(orderBy: Int) {
            this@WorkspaceGroupTaskViewFragment.orderBy = orderBy
            filterTaskBy = 2
            getTaskByProgressStatus(workspaceGroupTaskArrayList)
        }

        override fun filterByStage(orderBy: Int) {
            this@WorkspaceGroupTaskViewFragment.orderBy = orderBy
            filterTaskBy = 0
            getTaskByProgressStatus(workspaceGroupTaskArrayList)
        }

    }
    private val taskCallback = object : AppInterface.WorkspaceGroupTask.Task {
        override fun onTaskSelected(content: WorkspaceGroupTaskModel) {
            val bundle = Bundle()
            bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, content.dtId)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupTaskViewFragment_to_privateTaskFullDetailsFragment,
                bundle
            )
        }

        override fun onTaskDeleted(content: WorkspaceGroupTaskModel) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WorkspaceGroupTaskFragmentBinding.bind(view)

        groupData =
            requireArguments().getSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM) as WorkspaceGroupModel
        setupViewModel()
        setupObserver()

        binding.textViewHeader1.text = groupData.name
        binding.textViewCardTitle.text = groupData.description

        workspaceGroupTaskViewAdapter = WorkspaceGroupTaskViewAdapter(callback = taskCallback)

        binding.recyclerViewGroupTask.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = workspaceGroupTaskViewAdapter
        }

        binding.buttonCreateNewTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(AppConstant.Key.NAVIGATION_TASK_FLAG_KEY, AppConstant.Task.WORKSPACE_TASK)
            bundle.putLong(AppConstant.Key.NAVIGATION_WORKSPACE_ID, groupData.workspaceID)
            bundle.putLong(AppConstant.Key.NAVIGATION_GROUP_ID, groupData.gId)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupTaskViewFragment_to_addNewTodo,
                bundle
            )
        }


        val animation = PieChartAnimation(binding.pieChart, 360)
        animation.duration = 5000
        binding.pieChart.startAnimation(animation)


        binding.buttonMoreOption.setOnClickListener {
            WorkspaceTaskBottomSheetDialog.newInstance(
                requireContext(),
                callback = filterCallback
            ).show(
                requireActivity().supportFragmentManager,
                WorkspaceTaskBottomSheetDialog::class.java.canonicalName
            )
        }

        binding.imageButtonSearch.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(AppConstant.Key.NAVIGATION_WORKSPACE_ID, groupData.workspaceID)
            bundle.putLong(AppConstant.Key.NAVIGATION_GROUP_ID, groupData.gId)

            findNavController().navigate(
                R.id.action_workspace_workspaceGroupTaskViewFragment_to_searchTaskFragment,
                bundle
            )
        }
        binding.imageButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupObserver() {
        observerWorkspaceGroupTaskArrayList = Observer {
            it?.let {
                getTaskByProgressStatus(it as ArrayList<WorkspaceGroupTaskModel>)
            }
        }

        viewModel.getAllWorkspaceGroupTaskInLiveData(groupData.gId)
            .observe(viewLifecycleOwner, observerWorkspaceGroupTaskArrayList)
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        val viewModelFactory = ViewModelFactory(requireContext(), appRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[WorkspaceGroupTaskViewModel::class.java]

    }

    private fun getTaskByProgressStatus(arrayList: ArrayList<WorkspaceGroupTaskModel>) {

        workspaceGroupTaskArrayList = arrayList
        when (filterTaskBy) {
            0 -> {
                sortTaskByStatus(workspaceGroupTaskArrayList)
            }
            1 -> {
                sortTaskByDate(workspaceGroupTaskArrayList)
            }
            2 -> {
                sortTaskByPriority(workspaceGroupTaskArrayList)
            }
        }
        val finishedTask =
            arrayList.filter { it.taskStatus == AppConstant.Task.TASK_STATUS_COMPLETED }

        binding.textViewTotalTask.text = arrayList.size.toString()
        binding.textViewTaskFinished.text = finishedTask.size.toString()
        binding.textViewAverageTimeConsumed.text = String.format("00Hrs 00Min")


        Log.i(TAG, "getTaskByProgressStatus: $groupTaskByProgressModel")
        workspaceGroupTaskViewAdapter.updateDataset(groupTaskByProgressModel)
    }


    private fun sortTaskByStatus(arrayList: ArrayList<WorkspaceGroupTaskModel>) {
        val taskStatus = AppFunctions.getTaskStatus()
        groupTaskByProgressModel = ArrayList()
        val contactsByStatus = arrayListOf<Int>()

        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $taskStatus")

        if (orderBy == 1)
            taskStatus.sortedWith(compareBy {
                it.statusId
            })
        else
            taskStatus.sortedWith(compareByDescending {
                it.statusId
            })

        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $taskStatus")

        binding.progressbarCardProgress.updateItemSize(taskStatus.size)
        for (status in taskStatus) {
            val tasks: ArrayList<WorkspaceGroupTaskModel> = arrayList

            val taskByStatus = tasks.filter { it.taskStatus == status.statusId }

            contactsByStatus.add(taskByStatus.size)

            groupTaskByProgressModel.add(
                GroupTaskByProgressModel(
                    taskHeader = GroupTaskByProgressModel.TaskHeaderContent(
                        title = status.statusName,
                        subtitle = taskByStatus.size.toString()
                    ),
                    content = null
                )
            )

            for (task in taskByStatus) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = task
                    )
                )
            }

            if (taskByStatus.isEmpty()) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = null,
                        message = "No task in ${status.statusName}"
                    )
                )
            }


            binding.progressbarCardProgress.updateProgressSize(contactsByStatus, arrayList.size)
        }

    }

    private fun sortTaskByDate(arrayList: ArrayList<WorkspaceGroupTaskModel>) {
        groupTaskByProgressModel = ArrayList()

        val dataLists = arrayList.map {
            it.eventDateTime
        }.distinctBy {
            Calendar.getInstance().apply {
                timeInMillis = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
        }.sortedBy {
            it
        }


        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $dataLists")

        dataLists.distinct()

        if (orderBy == 1)
            dataLists.sortedBy { it }
        else
            dataLists.sortedByDescending { it }

        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $dataLists")

        val contactsByStatus = arrayListOf<Int>()

        binding.progressbarCardProgress.updateItemSize(dataLists.size)

        for (item in dataLists) {
            val tasks: ArrayList<WorkspaceGroupTaskModel> = arrayList

            val taskCount = tasks.filter {
                val endTime = Calendar.getInstance().apply {
                    timeInMillis = item
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.timeInMillis

                it.eventDateTime in item..endTime
            }

            contactsByStatus.add(taskCount.size)

            groupTaskByProgressModel.add(
                GroupTaskByProgressModel(
                    taskHeader = GroupTaskByProgressModel.TaskHeaderContent(
                        title = AppFunctions.getPatternByTimeInMilliSec(item, "dd MMM, yyyy"),
                        subtitle = taskCount.size.toString()
                    ),
                    content = null
                )
            )

            for (task in taskCount) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = task
                    )
                )
            }

            if (taskCount.isEmpty()) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = null,
                        message = "No task in ${
                            AppFunctions.getPatternByTimeInMilliSec(
                                item,
                                "dd MMM, yyyy"
                            )
                        }"
                    )
                )
            }
            binding.progressbarCardProgress.updateProgressSize(contactsByStatus, arrayList.size)
        }
    }


    private fun sortTaskByPriority(arrayList: ArrayList<WorkspaceGroupTaskModel>) {
        groupTaskByProgressModel = ArrayList()

        val dataLists = AppFunctions.getTaskPriority(requireContext())

        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $dataLists")

        if (orderBy == 1)
            dataLists.sortedWith(compareBy {
                it.statusId
            })
        else
            dataLists.sortedWith(compareByDescending {
                it.statusId
            })


        Log.i(TAG, "sortTaskByStatus: orderBy >> $orderBy  taskStatus >> $dataLists")


        dataLists.distinct()
        val contactsByStatus = arrayListOf<Int>()

        binding.progressbarCardProgress.updateItemSize(dataLists.size)

        for (item in dataLists) {
            val tasks: ArrayList<WorkspaceGroupTaskModel> = arrayList

            val taskCount = tasks.filter { it.taskPriority == item.statusId }

            contactsByStatus.add(taskCount.size)

            groupTaskByProgressModel.add(
                GroupTaskByProgressModel(
                    taskHeader = GroupTaskByProgressModel.TaskHeaderContent(
                        title = item.statusName,
                        subtitle = taskCount.size.toString()
                    ),
                    content = null
                )
            )

            for (task in taskCount) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = task
                    )
                )
            }

            if (taskCount.isEmpty()) {
                groupTaskByProgressModel.add(
                    GroupTaskByProgressModel(
                        taskHeader = null,
                        content = null,
                        message = "No task in ${item.statusName}"
                    )
                )
            }

            binding.progressbarCardProgress.updateProgressSize(contactsByStatus, arrayList.size)
        }
    }

}
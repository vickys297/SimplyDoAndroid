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
import com.example.simplydo.adapters.WorkspaceGroupTaskViewAdapter
import com.example.simplydo.components.PieChartAnimation
import com.example.simplydo.databinding.WorkspaceGroupTaskFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.GroupTaskByProgressModel
import com.example.simplydo.model.WorkspaceGroupTaskModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.*

internal val TAG = WorkspaceGroupTaskViewFragment::class.java.canonicalName
class WorkspaceGroupTaskViewFragment : Fragment(R.layout.workspace_group_task_fragment) {

    companion object {
        fun newInstance() = WorkspaceGroupTaskViewFragment()
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

        override fun onTaskDeleted() {
        }

    }
    private lateinit var observerWorkspaceGroupTaskArrayList: Observer<ArrayList<WorkspaceGroupTaskModel>>

    private lateinit var viewModel: WorkspaceGroupTaskViewModel
    private lateinit var binding: WorkspaceGroupTaskFragmentBinding
    private lateinit var groupData: WorkspaceGroupModel
    private lateinit var groupTaskByProgressModel: ArrayList<GroupTaskByProgressModel>
    private lateinit var workspaceGroupTaskViewAdapter: WorkspaceGroupTaskViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WorkspaceGroupTaskFragmentBinding.bind(view)
        setupViewModel()
        setupObserver()

        groupData =
            requireArguments().getSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM) as WorkspaceGroupModel

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
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWorkspaceGroupTask(groupData.gId)
    }


    private fun setupObserver() {
        observerWorkspaceGroupTaskArrayList = Observer {
            it?.let {
                getTaskByProgressStatus(it)
            }
        }
        viewModel.mutableWorkspaceGroupTask.observe(
            viewLifecycleOwner,
            observerWorkspaceGroupTaskArrayList
        )
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        val viewModelFactory = ViewModelFactory(requireContext(), appRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[WorkspaceGroupTaskViewModel::class.java]

    }

    private fun getTaskByProgressStatus(arrayList: ArrayList<WorkspaceGroupTaskModel>) {
        groupTaskByProgressModel = ArrayList()
        val taskStatus = AppFunctions.getTaskStatus()

        for (status in taskStatus) {
            val tasks: ArrayList<WorkspaceGroupTaskModel> = arrayList

            val taskByStatus = tasks.filter { it.taskStatus == status.statusId }
            val finishedTask = tasks.filter { it.taskStatus == AppConstant.Task.TASK_STATUS_COMPLETED }

            binding.textViewTotalTask.text = tasks.size.toString()
            binding.textViewTaskFinished.text = finishedTask.size.toString()
            binding.textViewAverageTimeConsumed.text = String.format("00Hrs 00Min")

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
        }

        Log.i(TAG, "getTaskByProgressStatus: $groupTaskByProgressModel")
        workspaceGroupTaskViewAdapter.updateDataset(groupTaskByProgressModel)
    }

}
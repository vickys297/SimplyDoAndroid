package com.example.simplydo.ui.fragments.organizationView.organizationTask

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.GroupTaskViewAdapter
import com.example.simplydo.databinding.OrganizationTaskFragmentBinding
import com.example.simplydo.model.GroupTaskByProgressModel
import com.example.simplydo.model.WorkspaceGroupsCollectionModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions

class OrganizationTaskViewFragment : Fragment(R.layout.organization_task_fragment) {

    companion object {
        fun newInstance() = OrganizationTaskViewFragment()
    }

    private lateinit var viewModel: OrganizationTaskViewModel
    private lateinit var binding: OrganizationTaskFragmentBinding
    private lateinit var groupData: WorkspaceGroupsCollectionModel
    private lateinit var groupTaskByProgressModel: ArrayList<GroupTaskByProgressModel>
    private lateinit var groupTaskViewAdapter: GroupTaskViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = OrganizationTaskFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[OrganizationTaskViewModel::class.java]

        groupData =
            requireArguments().getSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM) as WorkspaceGroupsCollectionModel

        getTaskByProgressStatus(groupData)

        binding.textViewHeader1.text = groupData.name
        binding.textViewCardTitle.text = groupData.description

        groupTaskViewAdapter = GroupTaskViewAdapter(dataSet = groupTaskByProgressModel)
        binding.recyclerViewGroupTask.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = groupTaskViewAdapter
        }
    }

    private fun getTaskByProgressStatus(groupData: WorkspaceGroupsCollectionModel) {
        groupTaskByProgressModel = ArrayList()
        val taskStatus = AppFunctions.getTaskStatus()

        for (status in taskStatus) {
            val tasks = groupData.task.filter { it.taskStatus == status.statusId }
            groupTaskByProgressModel.add(
                GroupTaskByProgressModel(
                    taskHeader = GroupTaskByProgressModel.TaskHeaderContent(
                        title = status.statusName,
                        subtitle = tasks.size.toString()
                    ),
                    content = null
                )
            )

            for (task in tasks) {
                GroupTaskByProgressModel(
                    taskHeader = null,
                    content = task
                )
            }
        }
    }

}
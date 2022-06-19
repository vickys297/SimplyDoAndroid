package com.example.simplydo.ui.fragments.searchTask

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.search.WorkspaceSearchTaskAdapter
import com.example.simplydo.databinding.SearchTaskFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.CommonBottomSheetDialogFragment
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchTaskFragment : Fragment(R.layout.search_task_fragment) {

    companion object {
        fun newInstance() = SearchTaskFragment()
    }

    private lateinit var binding: SearchTaskFragmentBinding
    private lateinit var viewModel: SearchTaskViewModel
    private lateinit var workspaceSearchTaskAdapter: WorkspaceSearchTaskAdapter
    private lateinit var commonBottomSheetDialogFragment: CommonBottomSheetDialogFragment

    private var workspaceId: Long = 0
    private var groupId: Long = 0

    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: Any, type: Int) {
            when (type) {
                AppConstant.Task.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task as WorkspaceGroupTaskModel)
                }
            }
        }
    }

    private val commonBottomSheetDialogInterface: CommonBottomSheetDialogInterface =
        object : CommonBottomSheetDialogInterface {
            override fun onPositiveButtonClick(content: Any) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val response = viewModel.deleteTask(content as WorkspaceGroupTaskModel)
                    if (response > 0) {
                        requireActivity().runOnUiThread {
                            AppFunctions.showSnackBar(
                                task = content,
                                view = binding.root,
                                message = "Task Removed",
                                actionButtonName = "Undo",
                                type = AppConstant.Task.TASK_ACTION_RESTORE,
                                undoInterface = undoInterface
                            )
                        }
                    }
                }
            }

            override fun onNegativeButtonClick(content: Any) {

            }
        }


    private val taskCallback = object : AppInterface.WorkspaceGroupTask.Task {
        override fun onTaskSelected(content: WorkspaceGroupTaskModel) {
            val bundle = Bundle()
            bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, content.dtId)
            findNavController().navigate(
                R.id.action_workspace_searchTaskFragment_to_privateTaskFullDetailsFragment,
                bundle
            )
        }

        override fun onTaskDeleted(content: WorkspaceGroupTaskModel) {
            commonBottomSheetDialogFragment = CommonBottomSheetDialogFragment.newInstance(
                requiredContext = requireContext(),
                title = "Authentication Required",
                message = "Do you wan to delete this #${content.title}",
                positiveButtonName = "Delete",
                commonBottomSheetDialogInterface = commonBottomSheetDialogInterface,
                modelClass = content
            )
            commonBottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                CommonBottomSheetDialogFragment::class.java.canonicalName
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchTaskFragmentBinding.bind(view)
        setupViewModel()

        workspaceSearchTaskAdapter = WorkspaceSearchTaskAdapter(taskCallback)


        binding.recyclerViewTaskList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = workspaceSearchTaskAdapter
        }

        workspaceId = requireArguments().getLong(AppConstant.Key.NAVIGATION_WORKSPACE_ID)
        groupId = requireArguments().getLong(AppConstant.Key.NAVIGATION_GROUP_ID)
        searchFilter("")
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                searchFilter(s.toString())
            }
        })

        binding.imageButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imageButtonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
        }
    }

    private fun searchFilter(searchFilterText: String) {
        lifecycleScope.launch {
            viewModel.searchTask(searchFilterText, workspaceId, groupId)
                .collect {
                    workspaceSearchTaskAdapter.submitData(it)
                }
        }
    }

    private fun setupViewModel() {
        val viewModelFactory = ViewModelFactory(
            requireContext(),
            AppRepository.getInstance(requireContext(), AppDatabase.getInstance(requireContext()))
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchTaskViewModel::class.java]
    }


}
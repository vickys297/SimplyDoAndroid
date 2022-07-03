package com.example.simplydo.ui.activity.privateWorkspace.workspaceGroupView

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.simplydo.R
import com.example.simplydo.adapters.WorkspaceGroupViewAdapter
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.databinding.GroupViewFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.calenderOptions.TodoOptions
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.WorkspaceSwitchBottomSheetDialog
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceGroupDialog.WorkspaceGroupOptionDialog
import com.example.simplydo.model.AccountModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.ui.fragments.dialog.AlertDialogFragment
import com.example.simplydo.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal val TAG = WorkspaceGroupViewFragment::class.java.canonicalName

class WorkspaceGroupViewFragment : Fragment(R.layout.group_view_fragment) {

    companion object {
        fun newInstance() = WorkspaceGroupViewFragment()
    }

    private lateinit var observerArraylistWorkspaceGroupModel: Observer<ArrayList<WorkspaceGroupModel>>
    private lateinit var observerWorkspaceGroupCount: Observer<Int>

    private lateinit var viewModelWorkspace: WorkspaceGroupViewViewModel
    private lateinit var binding: GroupViewFragmentBinding
    private lateinit var groupViewAdapter: WorkspaceGroupViewAdapter
    private lateinit var workspaceSwitchBottomSheetDialog: WorkspaceSwitchBottomSheetDialog
    private var workspaceID: Long = -1
    private lateinit var todoOptions: TodoOptions
    private lateinit var userData: AccountModel

    private var taskCount = 0

    private var workSpaceGroupOptionCallback = object : AppInterface.GroupViewOptionCallback {
        override fun onEdit(item: WorkspaceGroupModel) {
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP, item)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupViewFragment_to_editWorkspaceGroupFragment,
                bundle
            )
        }

        override fun onDelete(item: WorkspaceGroupModel) {
            val dialog = AlertDialogFragment.newInstance(
                message = "Are you sure do you want to delete ${item.name}?",
                cancelable = false,
                callback = object : AlertDialogFragment.Callback {
                    override fun onConfirm() {
                        viewModelWorkspace.deleteWorkspaceGroup(item)
                    }

                    override fun onClose() {
                    }
                }
            )
            dialog.show(requireActivity().supportFragmentManager, "")
        }
    }

    private var callback = object : AppInterface.GroupViewCallback {
        override fun onSelect(item: WorkspaceGroupModel) {
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM, item)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupViewFragment_to_workspaceGroupTaskViewFragment,
                bundle
            )
        }

        override fun onOption(item: WorkspaceGroupModel) {
            WorkspaceGroupOptionDialog.newInstance(workSpaceGroupOptionCallback, item)
                .show(requireActivity().supportFragmentManager, "")
        }
    }

    private val todoTaskOptionsInterface = object : TodoTaskOptionsInterface {
        override fun onCalenderView() {
            findNavController().navigate(R.id.action_toDoFragment_to_calenderFragment)
        }

        override fun onCompletedView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_COMPLETED)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

        override fun onPastTaskView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_PAST)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

        override fun onSettingsClicked() {
            findNavController().navigate(R.id.action_toDoFragment_to_mySettingActivity)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupViewFragmentBinding.bind(view)

        workspaceSwitchBottomSheetDialog =
            WorkspaceSwitchBottomSheetDialog.newInstance(
                requireContext(),
                callback = object : AppInterface.MyWorkspace.CreateWorkspaceDialog {
                    override fun onCreateNewWorkspace() {
                        findNavController().navigate(R.id.action_toDoFragment_to_createWorkspaceFragment)
                    }
                })

        todoOptions = TodoOptions.getInstance(requireContext(), todoTaskOptionsInterface)

        setupViewModel()
        setupObserver()
        workspaceID = AppPreference.getPreferences(
            AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
            -1L,
            requireContext()
        )

        val accentText = "<font color='#6200EE'>Workspace</font>"

        val workspaceTextTitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                String.format("My %s", accentText),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            Html.fromHtml(String.format("My %s", accentText))
        }

        userData = Gson().fromJson(
            AppPreference.getPreferences(
                AppConstant.Preferences.USER_DATA,
                requireContext()
            ), AccountModel::class.java
        )

        Glide
            .with(requireContext())
            .load(userData.profilePicture)
            .into(binding.profileImageView)

        viewModelWorkspace.getWorkSpaceCount(workspaceID)

        binding.textViewHeader.text = workspaceTextTitle

        binding.buttonCreateGroup.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(AppConstant.Key.NAVIGATION_WORKSPACE_ID, workspaceID)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupViewFragment_to_createNewWorkspaceGroupFragment,
                bundle
            )
        }
        binding.switchWorkspace.setOnClickListener {
            workspaceSwitchBottomSheetDialog.show(
                requireActivity().supportFragmentManager,
                WorkspaceSwitchBottomSheetDialog::class.java.canonicalName
            )
        }
        binding.profileImageView.setOnClickListener {
            findNavController().navigate(R.id.mySettingActivity)
        }

    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        groupViewAdapter = WorkspaceGroupViewAdapter(callback = callback)
        binding.recyclerViewGroupView.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = groupViewAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModelWorkspace.getWorkspaceGroup(workspaceID).collect {
                groupViewAdapter.submitData(it)
            }
        }
    }

    private fun setupObserver() {
        observerArraylistWorkspaceGroupModel = Observer {
            it?.let {
                taskCount = it.size
                binding.textViewParticipants.text = String.format("%d groups created", taskCount)
            }
        }
        viewModelWorkspace.mutableArrayWorkspaceGroupModel.observe(
            viewLifecycleOwner,
            observerArraylistWorkspaceGroupModel
        )

        observerWorkspaceGroupCount = Observer {
            it?.let {
                binding.textViewParticipants.text = String.format("%d groups created", it)
            }
        }

        viewModelWorkspace.mutableWorkspaceCount.observe(
            viewLifecycleOwner,
            observerWorkspaceGroupCount
        )
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModelWorkspace = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), appRepository)
        )[WorkspaceGroupViewViewModel::class.java]
    }

}
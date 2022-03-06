package com.example.simplydo.ui.activity.privateWorkspace.workspaceGroupView

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.WorkspaceGroupViewAdapter
import com.example.simplydo.databinding.GroupViewFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.WorkspaceSwitchBottomSheetDialog
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.*

internal val TAG = WorkspaceGroupViewFragment::class.java.canonicalName

class WorkspaceGroupViewFragment : Fragment(R.layout.group_view_fragment) {

    companion object {
        fun newInstance() = WorkspaceGroupViewFragment()
    }

    private lateinit var observerArraylistWorkspaceGroupModel: Observer<ArrayList<WorkspaceGroupModel>>
    private lateinit var viewModelWorkspace: WorkspaceGroupViewViewModel
    private lateinit var binding: GroupViewFragmentBinding
    private lateinit var groupViewAdapter: WorkspaceGroupViewAdapter
    private lateinit var workspaceSwitchBottomSheetDialog: WorkspaceSwitchBottomSheetDialog
    private var workspaceID: Long = -1


    private var taskCount = 0

    private var callback = object : AppInterface.GroupViewCallback {
        override fun onSelect(item: WorkspaceGroupModel) {
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM, item)
            findNavController().navigate(
                R.id.action_workspace_workspaceGroupViewFragment_to_workspaceGroupTaskViewFragment,
                bundle
            )
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

        setupViewModel()
        setupObserver()
        workspaceID = AppPreference.getPreferences(
            AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
            -1L,
            requireContext()
        )

        viewModelWorkspace.getWorkspaceGroup(workspaceID)


        val accentText = "<font color='#6200EE'>Workspace</font>"

        val workspaceTextTitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                String.format("My %s", accentText),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            Html.fromHtml(String.format("My %s", accentText))
        }

        binding.textViewHeader.text = workspaceTextTitle
        binding.textViewParticipants.text = String.format("%d groups created", taskCount)

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
    }

    private fun setupObserver() {
        observerArraylistWorkspaceGroupModel = Observer {

            it?.let {
                taskCount = it.size
                binding.textViewParticipants.text = String.format("%d groups created", taskCount)

                groupViewAdapter = WorkspaceGroupViewAdapter(dataset = it, callback = callback)

                binding.recyclerViewGroupView.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 2)
                    adapter = groupViewAdapter
                }
            }

        }
        viewModelWorkspace.mutableArrayWorkspaceGroupModel.observe(
            viewLifecycleOwner,
            observerArraylistWorkspaceGroupModel
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
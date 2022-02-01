package com.example.simplydo.ui.fragments.organizationView

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.WorkspaceGroupViewAdapter
import com.example.simplydo.databinding.GroupViewFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppInterface
import com.example.simplydo.utlis.AppRepository
import com.example.simplydo.utlis.ViewModelFactory

internal val TAG = GroupViewFragment::class.java.canonicalName

class GroupViewFragment : Fragment(R.layout.group_view_fragment) {

    companion object {
        fun newInstance() = GroupViewFragment()
    }

    private lateinit var observerArraylistWorkspaceGroupModel: Observer<ArrayList<WorkspaceGroupModel>>
    private lateinit var viewModelWorkspace: WorkspaceGroupViewViewModel
    private lateinit var binding: GroupViewFragmentBinding
    private lateinit var groupViewAdapter: WorkspaceGroupViewAdapter

    private var taskCount = 0

    private var callback = object : AppInterface.GroupViewCallback {
        override fun onSelect(item: WorkspaceGroupModel) {
            Log.i(TAG, "onSelect: ")
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP_ITEM, item)
            findNavController().navigate(
                R.id.action_groupViewFragment_to_organizationTaskFragment,
                bundle
            )
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupViewFragmentBinding.bind(view)
        setupViewModel()
        setupObserver()
        viewModelWorkspace.getWorkspaceGroup()

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
            findNavController().navigate(R.id.action_groupViewFragment_to_createNewWorkspaceGroupFragment)
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
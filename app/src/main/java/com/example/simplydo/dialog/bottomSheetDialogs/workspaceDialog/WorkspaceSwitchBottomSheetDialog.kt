package com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.adapters.MyWorkspaceAdapter
import com.example.simplydo.databinding.WorkspaceBottomSheetDialogBinding
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.ui.activity.personalWorkspace.PersonalWorkspaceActivity
import com.example.simplydo.ui.activity.privateWorkspace.WorkspaceActivity
import com.example.simplydo.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WorkspaceSwitchBottomSheetDialog(
    val requireContext: Context,
    private val createNewWorkspaceDialog: AppInterface.MyWorkspace.CreateWorkspaceDialog
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            requireContext: Context,
            callback: AppInterface.MyWorkspace.CreateWorkspaceDialog
        ) = WorkspaceSwitchBottomSheetDialog(requireContext, callback)
    }


    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var observerWorkspaceArrayList: Observer<ArrayList<WorkspaceModel>>
    private lateinit var viewModelSwitch: WorkspaceSwitchBottomSheetDialogViewModel
    private lateinit var binding: WorkspaceBottomSheetDialogBinding
    private lateinit var myWorkspaceAdapter: MyWorkspaceAdapter

    private val callback = object : AppInterface.MyWorkspace.Callback {
        override fun onSelect(item: WorkspaceModel) {
            val currentWorkspace = AppPreference.getPreferences(
                AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
                item.wId,
                requireActivity()
            )
            if (item.wId != currentWorkspace) {
                if (item.wId == -1L) {
                    val bundle = Bundle()
                    bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE, item)
                    val intent = Intent(requireActivity(), PersonalWorkspaceActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                } else {
                    val bundle = Bundle()
                    bundle.putSerializable(AppConstant.Key.NAVIGATION_WORKSPACE, item)
                    val intent = Intent(requireActivity(), WorkspaceActivity::class.java)
                    intent.putExtras(bundle)
                    requireActivity().startActivity(intent)

                    requireActivity().finish()
                }
                AppPreference.storePreferences(
                    AppConstant.Preferences.CURRENT_ACTIVE_WORKSPACE,
                    item.wId,
                    requireActivity()
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WorkspaceBottomSheetDialogBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupObserver()

        myWorkspaceAdapter = MyWorkspaceAdapter(callback = callback)

        binding.linearLayoutWorkspaceContent.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myWorkspaceAdapter
        }
        viewModelSwitch.getMyWorkspace()
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.modelBottomSheet)

        binding.buttonCreateWorkspace.setOnClickListener {
            createNewWorkspaceDialog.onCreateNewWorkspace()
            dismiss()
        }
    }

    private fun setupObserver() {
        observerWorkspaceArrayList = Observer {
            Log.i("observerWorkspaceArrayList", "setupObserver: $it")
            it?.let {
                myWorkspaceAdapter.loadData(it)
                standardBottomSheetBehavior.setPeekHeight(500, true)
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        viewModelSwitch.mutableWorkspaceModel.observe(
            viewLifecycleOwner,
            observerWorkspaceArrayList
        )
        viewModelSwitch.mutableWorkspaceModel.postValue(null)
    }

    private fun setupViewModel() {
        val appRepository =
            AppRepository.getInstance(requireContext(), AppDatabase.getInstance(requireContext()))
        val viewModelFactory = ViewModelFactory(requireContext(), appRepository)
        viewModelSwitch = ViewModelProvider(
            this,
            viewModelFactory
        )[WorkspaceSwitchBottomSheetDialogViewModel::class.java]

        binding.apply {
            viewModel = this@WorkspaceSwitchBottomSheetDialog.viewModelSwitch
            lifecycleOwner = this@WorkspaceSwitchBottomSheetDialog
            executePendingBindings()
        }
    }

}
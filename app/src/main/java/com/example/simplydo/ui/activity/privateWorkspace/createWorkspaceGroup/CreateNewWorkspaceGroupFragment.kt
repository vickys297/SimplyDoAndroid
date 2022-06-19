package com.example.simplydo.ui.activity.privateWorkspace.createWorkspaceGroup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.workspace.ParticipantsAdapter
import com.example.simplydo.databinding.CreateNewWorkspaceGroupFragmentBinding
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.AccountModel
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.model.UserIdModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utils.*
import com.google.gson.Gson

internal val TAG = CreateNewWorkspaceGroupFragment::class.java.canonicalName

class CreateNewWorkspaceGroupFragment : Fragment(R.layout.create_new_workspace_group_fragment),
    View.OnClickListener {

    companion object {
        fun newInstance() = CreateNewWorkspaceGroupFragment()
    }

    private lateinit var viewModel: CreateNewWorkspaceGroupViewModel
    private lateinit var binding: CreateNewWorkspaceGroupFragmentBinding

    private lateinit var participantsAdapter: ParticipantsAdapter
    private var participantsList: ArrayList<UserAccountModel> = arrayListOf()

    private var workspaceId: Long = 1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CreateNewWorkspaceGroupFragmentBinding.bind(view)
        setupViewModel()

        requireArguments().let {
            workspaceId = it.getLong(AppConstant.Key.NAVIGATION_WORKSPACE_ID)
        }

        binding.buttonCreateGroup.setOnClickListener(this@CreateNewWorkspaceGroupFragment)
        participantsAdapter = ParticipantsAdapter(isRemoveVisible = false, callback = null)



        binding.recyclerViewParticipants.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = participantsAdapter
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<UserAccountModel>>(
            AppConstant.Key.NAVIGATION_PARTICIPANT_KEY
        )?.observe(viewLifecycleOwner) {
            it?.let {
                participantsList = it
                participantsAdapter.updateDataset(it)
            }
        }

        binding.imageButtonAddParticipants.setOnClickListener {
            findNavController().navigate(R.id.action_createNewWorkspaceGroupFragment_to_selectParticipantsFragment)
        }
    }

    private fun setupViewModel() {
        val viewModelFactory = ViewModelFactory(
            requireContext(),
            AppRepository.getInstance(
                requireContext(),
                AppDatabase.getInstance(requireContext())
            )
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateNewWorkspaceGroupViewModel::class.java]
        binding.apply {
            this.viewModel = this@CreateNewWorkspaceGroupFragment.viewModel
            lifecycleOwner = this@CreateNewWorkspaceGroupFragment
            executePendingBindings()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.buttonCreateGroup.id -> {
                validateDetails()
            }
        }
    }

    private fun validateDetails() {
        if (binding.editTextName.text.isEmpty()) {
            binding.editTextName.error = "Required"
            AppFunctions.showSnackBar(requireView(), getString(R.string.group_name_required_note))
        } else {
            createNewWorkSpace()
        }
    }

    private fun createNewWorkSpace() {
        val newGroup = WorkspaceGroupModel(
            workspaceID = workspaceId,
            name = binding.editTextName.text.toString(),
            description = binding.editTextDescriptions.text.toString(),
            createdBy = UserIdModel(
                admin = Gson().fromJson(
                    AppPreference.getPreferences(
                        AppConstant.Preferences.USER_DATA,
                        requireContext()
                    ), AccountModel::class.java
                )
            ),
            people = participantsList
        )


        viewModel.insertNewGroup(newGroup)
        findNavController().navigateUp()
    }

}
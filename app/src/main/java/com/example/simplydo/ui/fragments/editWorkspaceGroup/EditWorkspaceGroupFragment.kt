package com.example.simplydo.ui.fragments.editWorkspaceGroup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.workspace.ParticipantsAdapter
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.databinding.FragmentEditWorkspaceGroupBinding
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.AppRepository
import com.example.simplydo.utils.ViewModelFactory

class EditWorkspaceGroupFragment : Fragment(R.layout.fragment_edit_workspace_group),
    View.OnClickListener {

    companion object {
        fun newInstance() = EditWorkspaceGroupFragment()
    }

    private lateinit var viewModel: EditWorkspaceGroupViewModel
    private lateinit var binding: FragmentEditWorkspaceGroupBinding

    private lateinit var participantsAdapter: ParticipantsAdapter
    private var participantsList: ArrayList<UserAccountModel> = arrayListOf()

    private lateinit var workspaceGroupModel: WorkspaceGroupModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditWorkspaceGroupBinding.bind(view)
        setupViewModel()

        requireArguments().let {
            workspaceGroupModel =
                it.getSerializable(AppConstant.Key.NAVIGATION_WORKSPACE_GROUP) as WorkspaceGroupModel
        }

        binding.buttonCreateGroup.setOnClickListener(this@EditWorkspaceGroupFragment)
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
                it.distinct()
                participantsList.addAll(it)
                participantsAdapter.updateDataset(it)
            }
        }

        binding.imageButtonAddParticipants.setOnClickListener {
            findNavController().navigate(R.id.action_workspace_editWorkspaceGroupFragment_to_selectParticipantsFragment)
        }

        loadWorkspacePeopleGroup(workspaceGroupModel)
    }

    private fun loadWorkspacePeopleGroup(workspaceGroupModel: WorkspaceGroupModel) {
        binding.editTextName.setText(workspaceGroupModel.name)
        binding.editTextDescriptions.setText(workspaceGroupModel.description)
        participantsList = workspaceGroupModel.people
        participantsAdapter.updateDataset(workspaceGroupModel.people)
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
            ViewModelProvider(this, viewModelFactory)[EditWorkspaceGroupViewModel::class.java]
        binding.apply {
            this.viewModel = this@EditWorkspaceGroupFragment.viewModel
            lifecycleOwner = this@EditWorkspaceGroupFragment
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

        workspaceGroupModel.name = binding.editTextName.text.toString().trim()
        workspaceGroupModel.description = binding.editTextDescriptions.text.toString().trim()
        workspaceGroupModel.people = participantsList

        viewModel.updateWorkspaceGroup(workspaceGroupModel)
        findNavController().navigateUp()
    }

}
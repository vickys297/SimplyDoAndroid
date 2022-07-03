package com.example.simplydo.dialog.bottomSheetDialogs.workspaceGroupDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.databinding.BottomSheetDialogFragmentWorkspaceGroupOptionBinding
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utils.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WorkspaceGroupOptionDialog(
    private val workSpaceGroupOptionCallback: AppInterface.GroupViewOptionCallback,
    private val item: WorkspaceGroupModel
) :
    BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            workSpaceGroupOptionCallback: AppInterface.GroupViewOptionCallback,
            item: WorkspaceGroupModel
        ) =
            WorkspaceGroupOptionDialog(workSpaceGroupOptionCallback, item)
    }

    private lateinit var viewModel: WorkspaceGroupOptionDialogViewModel
    private lateinit var binding: BottomSheetDialogFragmentWorkspaceGroupOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFragmentWorkspaceGroupOptionBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WorkspaceGroupOptionDialogViewModel::class.java]

        binding.buttonEdit.setOnClickListener {
            workSpaceGroupOptionCallback.onEdit(item)
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            workSpaceGroupOptionCallback.onDelete(item)
            dismiss()
        }

    }

}
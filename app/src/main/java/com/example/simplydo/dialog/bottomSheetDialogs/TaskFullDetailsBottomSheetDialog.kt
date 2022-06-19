package com.example.simplydo.dialog.bottomSheetDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.adapters.GroupViewAdapter
import com.example.simplydo.adapters.StageListAdapter
import com.example.simplydo.databinding.WorkspaceTaskFullDetailsBottomSheetDialogFragmentBinding
import com.example.simplydo.model.TaskStatusDataModel
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskFullDetailsBottomSheetDialog internal constructor(
    private val fragmentContent: Context,
    val callback: AppInterface.TaskFullDetailsCallBack
) :
    BottomSheetDialogFragment() {

    companion object {
        fun newInstance(content: Context, callback: AppInterface.TaskFullDetailsCallBack) =
            TaskFullDetailsBottomSheetDialog(content, callback)
    }

    private lateinit var viewModel: TaskFullDetailsBottomSheetDialogViewModel
    private lateinit var binding: WorkspaceTaskFullDetailsBottomSheetDialogFragmentBinding
    private lateinit var stageListAdapter: StageListAdapter

    private val fullTaskStageCallback = object :
        AppInterface.TaskFullDetailsCallBack.TaskFullDetailsStageCallback {
        override fun onStageSelected(item: TaskStatusDataModel) {
            callback.onStageSelect(item)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            WorkspaceTaskFullDetailsBottomSheetDialogFragmentBinding.inflate(
                LayoutInflater.from(
                    fragmentContent
                ), container, false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this)[TaskFullDetailsBottomSheetDialogViewModel::class.java]

        stageListAdapter = StageListAdapter(callback = fullTaskStageCallback)

        binding.recyclerViewStage.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = stageListAdapter
            addItemDecoration(GroupViewAdapter.OverlapRecyclerViewDecoration(4, -25))
        }
        
        val statusList = AppFunctions.getTaskStatus()
        stageListAdapter.updateDatSet(statusList)

        binding.buttonViewCalendar.setOnClickListener {
            callback.onViewCalendar()
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            callback.onDelete()
            dismiss()
        }

        binding.buttonAddParticipants.setOnClickListener {
            callback.onAddParticipants()
            dismiss()
        }
    }

}
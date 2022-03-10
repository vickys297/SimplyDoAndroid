package com.example.simplydo.dialog.bottomSheetDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.databinding.WorkspaceTaskFullDetailsBottomSheetDialogFragmentBinding
import com.example.simplydo.utlis.AppFunctions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskFullDetailsBottomSheetDialog internal constructor(private val fragmentContent: Context) :
    BottomSheetDialogFragment() {

    companion object {
        fun newInstance(content: Context) = TaskFullDetailsBottomSheetDialog(content)
    }

    private lateinit var viewModel: TaskFunnDetailsBottomSheetDialogViewModel
    private lateinit var binding: WorkspaceTaskFullDetailsBottomSheetDialogFragmentBinding

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
            ViewModelProvider(this)[TaskFunnDetailsBottomSheetDialogViewModel::class.java]

        val statusList = AppFunctions.getTaskStatus()

        for (item  in statusList){
            val linearLayout = LinearLayout(requireContext())
            linearLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = (LinearLayout.HORIZONTAL)

            val textView = TextView(requireContext())
            textView.apply {
                text =item.statusName
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            linearLayout.addView(textView)

            binding.linearLayoutStages.addView(linearLayout)
        }
    }

}
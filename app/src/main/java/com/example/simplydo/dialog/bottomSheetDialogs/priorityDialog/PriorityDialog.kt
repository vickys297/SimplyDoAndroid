package com.example.simplydo.dialog.bottomSheetDialogs.priorityDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.simplydo.R
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PriorityDialog(private val callback: AppInterface.PriorityDialog.Callback) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_priority_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_high_priority).setOnClickListener {
            callback.onSelect(AppConstant.TaskPriority.HIGH_PRIORITY)
            dismiss()
        }

        view.findViewById<Button>(R.id.button_medium_priority).setOnClickListener {
            callback.onSelect(AppConstant.TaskPriority.MEDIUM_PRIORITY)
            dismiss()
        }

        view.findViewById<Button>(R.id.button_low_priority).setOnClickListener {
            callback.onSelect(AppConstant.TaskPriority.LOW_PRIORITY)
            dismiss()
        }
    }

    companion object {
        fun newInstance(callback: AppInterface.PriorityDialog.Callback) = PriorityDialog(callback)
    }
}
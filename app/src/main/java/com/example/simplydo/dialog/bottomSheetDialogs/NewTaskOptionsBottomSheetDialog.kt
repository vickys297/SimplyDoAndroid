package com.example.simplydo.dialog.bottomSheetDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.simplydo.R
import com.example.simplydo.utils.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewTaskOptionsBottomSheetDialog(
    val callback: AppInterface.NewTaskDialogCallback,
    private val parentContext: Context
) : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(parentContext).inflate(
            R.layout.fragment_new_task_options_bottom_sheet_dialog,
            container,
            false
        )
        view.findViewById<Button>(R.id.button_remainder).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_task).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_close).setOnClickListener(this)
        return view
    }

    companion object {
        fun getInstance(context: Context, callback: AppInterface.NewTaskDialogCallback) =
            NewTaskOptionsBottomSheetDialog(parentContext = context, callback = callback)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_remainder -> {
                callback.onOptionSelected(1)
                dismiss()
            }
            R.id.button_task -> {
                callback.onOptionSelected(2)
                dismiss()
            }
            R.id.button_close -> {
                callback.onClose()
                dismiss()
            }
        }
    }
}
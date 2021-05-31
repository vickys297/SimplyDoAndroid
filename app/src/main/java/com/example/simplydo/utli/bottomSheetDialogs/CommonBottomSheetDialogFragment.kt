package com.example.simplydo.utli.bottomSheetDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.simplydo.R
import com.example.simplydo.utli.CommonBottomSheetDialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val DIALOG_TITLE = "param1"
private const val DIALOG_MESSAGE = "param2"
private const val DIALOG_POSITIVE_BUTTON_NAME = "param3"

class CommonBottomSheetDialogFragment(private val commonBottomSheetDialogInterface: CommonBottomSheetDialogInterface) : BottomSheetDialogFragment() {

    private var dialogTitle: String = ""
    private var dialogMessage: String = ""
    private var dialogPositiveButtonName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogTitle = it.getString(DIALOG_TITLE).toString()
            dialogMessage = it.getString(DIALOG_MESSAGE).toString()
            dialogPositiveButtonName = it.getString(DIALOG_POSITIVE_BUTTON_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        isCancelable = false
        return inflater.inflate(R.layout.fragment_common_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnClose = view.findViewById<ImageButton>(R.id.imageButtonCloseDialog)

        btnCancel.apply {
            text = dialogPositiveButtonName
            setOnClickListener {
                commonBottomSheetDialogInterface.onPositiveButtonClick()
            }
        }
        tvTitle.text = dialogTitle
        tvMessage.text = dialogMessage


        btnClose.setOnClickListener {
            dismiss()
        }


    }

    companion object {

        @JvmStatic
        fun newInstance(
            title: String,
            message: String,
            positiveButtonName: String,
            commonBottomSheetDialogInterface: CommonBottomSheetDialogInterface
        ) =
            CommonBottomSheetDialogFragment(commonBottomSheetDialogInterface).apply {
                arguments = Bundle().apply {
                    putString(DIALOG_TITLE, title)
                    putString(DIALOG_MESSAGE, message)
                    putString(DIALOG_POSITIVE_BUTTON_NAME, positiveButtonName)
                }
            }
    }
}
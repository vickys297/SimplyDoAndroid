package com.example.simplydo.dialog.bottomSheetDialogs.addTodoItem

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.simplydo.R
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal val TAG = AddTodoItemDialog::class.java.canonicalName

class AddTodoItemDialog(
    private val addItemInterface: AppInterface.TaskNoteTextItemListener,
    private val requireContent: Context,
    private val bundle: Bundle?
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return LayoutInflater.from(requireContent)
            .inflate(R.layout.fragment_add_item_bottom_sheet_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextContent = view.findViewById<EditText>(R.id.edit_text_content)
        val addAndRepeatButton = view.findViewById<Button>(R.id.button_add_repeat)
        val buttonClose = view.findViewById<ImageButton>(R.id.image_button_close)

        bundle?.let {
            val content = it.getString(AppConstant.Bundle.Key.TODO_TASK_TEXT)
            editTextContent.setText(content)
        }

        buttonClose.setOnClickListener {
            dismiss()
        }

        editTextContent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateContent(editTextContent)
                return@setOnEditorActionListener true
            }
            false
        }

        addAndRepeatButton.setOnClickListener {
            validateContent(editTextContent)
        }
    }

    private fun validateContent(editTextContent: EditText) {
        Log.i(TAG, "validateContent: $editTextContent")
        if (editTextContent.text.isNotEmpty()) {
            addItemInterface.onAdd(content = editTextContent.text.toString())
            editTextContent.text.clear()
        } else {
            editTextContent.error = "Field Required"
        }
    }

    companion object {
        fun newInstance(
            taskNoteTextItemListener: AppInterface.TaskNoteTextItemListener,
            bundle: Bundle? = null,
            context: Context
        ): AddTodoItemDialog {
            return AddTodoItemDialog(taskNoteTextItemListener, context, bundle)
        }

    }
}
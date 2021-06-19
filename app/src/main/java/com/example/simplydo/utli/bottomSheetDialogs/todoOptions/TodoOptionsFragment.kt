package com.example.simplydo.utli.bottomSheetDialogs.todoOptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.TodoOptionsDialogFragmentsBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.TodoOptionDialogFragments
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoOptionsFragment internal constructor(
    private val todoOptionDialogFragments: TodoOptionDialogFragments,
    private val item: TodoModel
) :
    BottomSheetDialogFragment() {


    lateinit var binding: TodoOptionsDialogFragmentsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = TodoOptionsDialogFragmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOptionEdit.setOnClickListener {
            todoOptionDialogFragments.onEdit(item)
            dismiss()
        }

        binding.btnOptionDelete.setOnClickListener {
            todoOptionDialogFragments.onDelete(item)
            dismiss()
        }

        if (item.isCompleted) {
            binding.btnOptionRestore.apply {
                isClickable = true
                isEnabled = true
                setOnClickListener {
                    todoOptionDialogFragments.onRestore(item)
                    dismiss()
                }
            }
        } else {
            binding.btnOptionRestore.apply {
                isClickable = false
                isEnabled = false
            }
        }
    }


    companion   object {
        fun newInstance(todoOptionDialogFragments: TodoOptionDialogFragments, item: TodoModel) =
            TodoOptionsFragment(todoOptionDialogFragments, item)
    }
}
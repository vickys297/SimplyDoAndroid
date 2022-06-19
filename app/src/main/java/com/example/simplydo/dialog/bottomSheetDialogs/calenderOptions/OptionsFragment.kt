package com.example.simplydo.dialog.bottomSheetDialogs.calenderOptions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentOptionsBinding
import com.example.simplydo.utils.TodoTaskOptionsInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoOptions(
    private val fragmentContext: Context,
    private val todoTaskOptionsInterface: TodoTaskOptionsInterface
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val layoutInflater = LayoutInflater.from(fragmentContext)
        binding = FragmentOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCalenderView.setOnClickListener {
            todoTaskOptionsInterface.onCalenderView()
            dismiss()
        }


        binding.buttonCompletedView.setOnClickListener {
            todoTaskOptionsInterface.onCompletedView()
            dismiss()
        }


        binding.buttonPastTaskView.setOnClickListener {
            todoTaskOptionsInterface.onPastTaskView()
            dismiss()
        }


    }

    companion object {
        fun getInstance(
            fragmentContext: Context,
            todoTaskOptionsInterface: TodoTaskOptionsInterface
        ) = TodoOptions(fragmentContext, todoTaskOptionsInterface)
    }
}
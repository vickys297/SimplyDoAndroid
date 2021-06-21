package com.example.simplydo.bottomSheetDialogs.calenderOptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentOptionsBinding
import com.example.simplydo.utli.TodoTaskOptionsInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoOptions(private val todoTaskOptionsInterface: TodoTaskOptionsInterface) :
    BottomSheetDialogFragment() {


    private lateinit var binding: FragmentOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOptionsBinding.inflate(inflater, container, false)
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
        fun newInstance(todoTaskOptionsInterface: TodoTaskOptionsInterface) =
            TodoOptions(todoTaskOptionsInterface)
    }
}
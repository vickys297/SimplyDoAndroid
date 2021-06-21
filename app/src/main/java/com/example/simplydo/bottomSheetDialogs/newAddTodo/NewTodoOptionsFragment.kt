package com.example.simplydo.bottomSheetDialogs.newAddTodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.databinding.NewTodoOptionsFragmentBinding
import com.example.simplydo.utli.NewTodoOptionsFragmentsInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Deprecated("Do not use options")
class NewTodoOptionsFragment(private val newTodoOptionsFragmentsInterface: NewTodoOptionsFragmentsInterface) :
    BottomSheetDialogFragment() {

    companion object {
        fun newInstance(newTodoOptionsFragmentsInterface: NewTodoOptionsFragmentsInterface) =
            NewTodoOptionsFragment(newTodoOptionsFragmentsInterface)
    }

    private lateinit var viewModel: NewTodoOptionsViewModel
    private lateinit var binding: NewTodoOptionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = NewTodoOptionsFragmentBinding.inflate(inflater, container, false)

        setupViewModel()
        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(NewTodoOptionsViewModel::class.java)

        binding.apply {
            lifecycleOwner = this@NewTodoOptionsFragment
            executePendingBindings()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.buttonAddAttachments.setOnClickListener {
            newTodoOptionsFragmentsInterface.onAddAttachments()
            dismiss()
        }

        binding.buttonCloseOption.setOnClickListener {
            newTodoOptionsFragmentsInterface.onClose()
            dismiss()
        }


    }

}
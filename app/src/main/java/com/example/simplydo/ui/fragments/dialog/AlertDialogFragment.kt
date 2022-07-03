package com.example.simplydo.ui.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.databinding.FragmentAlertDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertDialogFragment(
    private val message: String,
    private val cancelable: Boolean,
    private val callback: Callback
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            message: String,
            cancelable: Boolean,
            callback: AlertDialogFragment.Callback
        ) = AlertDialogFragment(
            message, cancelable, callback
        )
    }

    private lateinit var viewModel: AlertDialogViewModel
    private lateinit var binding: FragmentAlertDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertDialogBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AlertDialogViewModel::class.java]

        isCancelable = cancelable

        binding.textViewMessage.text = message
        binding.buttonConfirm.setOnClickListener {
            callback.onConfirm()
            dismiss()
        }
        binding.imageButtonClose.setOnClickListener {
            callback.onClose()
            dismiss()
        }

    }


    interface Callback {
        fun onConfirm()
        fun onClose()
    }


}
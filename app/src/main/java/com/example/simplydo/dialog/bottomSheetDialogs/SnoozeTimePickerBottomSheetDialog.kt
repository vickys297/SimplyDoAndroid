package com.example.simplydo.dialog.bottomSheetDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentSnoozeTimePickerBottomSheetDialogBinding
import com.example.simplydo.utlis.SnoozeTimeInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SnoozeTimePickerBottomSheetDialog(
    val fragmentContext: Context,
    val callback: SnoozeTimeInterface
) :
    BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSnoozeTimePickerBottomSheetDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSnoozeTimePickerBottomSheetDialogBinding.inflate(
            LayoutInflater.from(fragmentContext), container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.button10Mins.setOnClickListener(this@SnoozeTimePickerBottomSheetDialog)
        binding.button15Mins.setOnClickListener(this@SnoozeTimePickerBottomSheetDialog)
        binding.button5Mins.setOnClickListener(this@SnoozeTimePickerBottomSheetDialog)
        binding.buttonClose.setOnClickListener(this@SnoozeTimePickerBottomSheetDialog)

    }

    companion object {
        fun newInstance(
            fragmentContext: Context,
            callback: SnoozeTimeInterface
        ) = SnoozeTimePickerBottomSheetDialog(fragmentContext, callback)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.button15Mins.id -> {
                callback.onSnoozeTimeSelect(15)
                dismiss()
            }
            binding.button10Mins.id -> {
                callback.onSnoozeTimeSelect(10)
                dismiss()
            }
            binding.button5Mins.id -> {
                callback.onSnoozeTimeSelect(5)
                dismiss()
            }
            binding.buttonClose.id -> {
                dismiss()
            }
        }
    }
}
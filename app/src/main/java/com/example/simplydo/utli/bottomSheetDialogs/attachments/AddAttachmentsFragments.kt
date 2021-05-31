package com.example.simplydo.utli.bottomSheetDialogs.attachments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentAddAttachmentsFragmentsBinding
import com.example.simplydo.utli.AddAttachmentInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddAttachmentsFragments(private val addAttachmentInterface: AddAttachmentInterface) :
    BottomSheetDialogFragment() {

    lateinit var binding: FragmentAddAttachmentsFragmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddAttachmentsFragmentsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAddContact.setOnClickListener {
            addAttachmentInterface.onAddContact()
            dismiss()
        }

        binding.buttonAddAudio.setOnClickListener {
            addAttachmentInterface.onAddAudio()
            dismiss()
        }

        binding.btnOpenGallery.setOnClickListener {
            addAttachmentInterface.onOpenGallery()
            dismiss()
        }



    }

    companion object {

        @JvmStatic
        fun newInstance(addAttachmentInterface: AddAttachmentInterface) =
            AddAttachmentsFragments(addAttachmentInterface)
    }
}
package com.example.simplydo.dialog.bottomSheetDialogs.attachments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentAddAttachmentsFragmentsBinding
import com.example.simplydo.utils.AddAttachmentInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddAttachmentsFragments(
    private val fragmentContext: Context,
    private val addAttachmentInterface: AddAttachmentInterface
) :
    BottomSheetDialogFragment() {

    lateinit var binding: FragmentAddAttachmentsFragmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val layoutInflater = LayoutInflater.from(fragmentContext)
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

        binding.btnAddLocation.setOnClickListener {
            addAttachmentInterface.onAddLocation()
            dismiss()
        }

        binding.btnCancelTask.setOnClickListener {
            addAttachmentInterface.onCancelTask()
            dismiss()
        }

        binding.buttonAddDocument.setOnClickListener {
            addAttachmentInterface.onAddDocument()
            dismiss()
        }


    }

    companion object {

        @JvmStatic
        fun newInstance(context: Context,addAttachmentInterface: AddAttachmentInterface) =
            AddAttachmentsFragments(context,addAttachmentInterface)
    }
}
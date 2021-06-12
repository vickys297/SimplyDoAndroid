package com.example.simplydo.ui.fragments.todoListFullDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFullDetailsFragmentBinding
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.AudioAttachmentInterface
import com.example.simplydo.utli.ContactAttachmentInterface
import com.example.simplydo.utli.GalleryAttachmentInterface
import com.example.simplydo.utli.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.utli.adapters.newTodotask.ContactAttachmentAdapter
import com.example.simplydo.utli.adapters.newTodotask.GalleryAttachmentAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

internal val TAG = TodoFullDetailsFragment::class.java.canonicalName

class TodoFullDetailsFragment : Fragment(R.layout.todo_full_details_fragment) {

    companion object {
        fun newInstance() = TodoFullDetailsFragment()
    }

    private lateinit var _binding: TodoFullDetailsFragmentBinding
    private val binding: TodoFullDetailsFragmentBinding get() = _binding

    private lateinit var viewModel: TodoFullDetailsViewModel

    // all adapter
    private lateinit var audioAttachmentAdapter: AudioAttachmentAdapter
    private lateinit var galleryAttachmentAdapter: GalleryAttachmentAdapter
    private lateinit var contactAttachmentAdapter: ContactAttachmentAdapter


    // all interfaces

    private val audioAttachmentInterface =
        object : AudioAttachmentInterface {
            override fun onAudioSelect(item: AudioModel) {

            }

        }
    private val galleryAttachmentInterface =
        object : GalleryAttachmentInterface {
            override fun onItemSelect(item: GalleryModel) {

            }
        }

    private val contactAttachmentInterface = object : ContactAttachmentInterface {
        override fun onContactSelect(item: ContactModel) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFullDetailsFragmentBinding.bind(view)

        setupBinding()

        viewModel = ViewModelProvider(this).get(TodoFullDetailsViewModel::class.java)

        arguments?.let {
            Log.d(TAG, "onViewCreated: ${it.getSerializable("todo")}")

            val todoModel = it.getSerializable("todo") as TodoModel
            binding.tvTitle.text = todoModel.title
            binding.tvTodo.text = todoModel.todo
            binding.textViewDateExpired.visibility = todoModel.dateExpiredVisibility()
            binding.textViewPriority.visibility = todoModel.isVisible()

            if (todoModel.eventTime.isEmpty()) {
                binding.textViewEventDateAndTime.text = todoModel.getEventDateTextValue()
            } else {
                binding.textViewEventDateAndTime.text =
                    String.format(
                        "%s @ %s",
                        todoModel.getEventDateTextValue(),
                        AppFunctions.convertTimeStringToDisplayFormat(todoModel.eventTime)
                    )
            }

            if (
                todoModel.audioAttachments.isEmpty() &&
                todoModel.imageAttachments.isEmpty() &&
                todoModel.contactAttachments.isEmpty() &&
                todoModel.locationData.isEmpty()
            ) {
                binding.noAttachmentFound.root.visibility = View.VISIBLE
            } else {
                binding.noAttachmentFound.root.visibility = View.GONE
            }


            if (todoModel.audioAttachments.isEmpty()) {
                binding.linearLayoutAudioAttachment.visibility = View.GONE
            } else {
                audioAttachmentAdapter.updateDataSet(todoModel.audioAttachments)
                binding.linearLayoutAudioAttachment.visibility = View.VISIBLE
            }

            if (todoModel.imageAttachments.isEmpty()) {
                binding.linearLayoutGalleryAttachment.visibility = View.GONE
            } else {
                galleryAttachmentAdapter.updateDataset(todoModel.imageAttachments)
                binding.linearLayoutGalleryAttachment.visibility = View.VISIBLE
            }

            if (todoModel.contactAttachments.isEmpty()) {
                binding.linearLayoutContactAttachment.visibility = View.GONE
            } else {
                contactAttachmentAdapter.updateDataSet(todoModel.contactAttachments)
                binding.linearLayoutContactAttachment.visibility = View.VISIBLE
            }

            if (todoModel.documentAttachments.isEmpty()) {
                binding.linearDocumentAttachment.visibility = View.GONE
            } else {
                binding.linearDocumentAttachment.visibility = View.VISIBLE
            }

            if (todoModel.locationData.isEmpty()) {
                binding.linearLocationAttachment.visibility = View.GONE
            } else {
                binding.linearLocationAttachment.visibility = View.VISIBLE
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?

                val latLng = todoModel.locationData.split(",".toRegex()).toTypedArray()

                val latitude: Double = latLng[0].toDouble()
                val longitude: Double = latLng[1].toDouble()

                mapFragment?.getMapAsync { googleMap ->
                    googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.map_styled_json
                        )
                    )
                    googleMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(
                                LatLng(latitude, longitude), 12f
                            )
                    )
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .icon(
                                AppFunctions.getDrawableToBitmap(
                                    R.drawable.ic_map_marker,
                                    requireActivity()
                                )
                            )
                    )
                }

                val mapUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                binding.buttonViewInMap.setOnClickListener {
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }


    }

    private fun setupBinding() {
        audioAttachmentAdapter =
            AudioAttachmentAdapter(audioAttachmentInterface = audioAttachmentInterface)
        galleryAttachmentAdapter =
            GalleryAttachmentAdapter(requireContext(), galleryAttachmentInterface)
        contactAttachmentAdapter =
            ContactAttachmentAdapter(requireContext(), contactAttachmentInterface)

        binding.recyclerViewAudioAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = audioAttachmentAdapter
        }

        binding.recyclerViewGalleryAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAttachmentAdapter
        }

        binding.recyclerViewContactAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = contactAttachmentAdapter
        }
    }

}